package com.example.demo.service;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

import org.springframework.stereotype.Service;

import com.example.demo.exception.CircuitOpenException;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CircuitBreakerService {

    // Circuit has 3 states:
    // CLOSED  → everything normal, requests go through
    // OPEN    → too many failures, block all requests
    // HALF_OPEN → testing if system recovered, allow one request
    public enum CircuitState {
        CLOSED, OPEN, HALF_OPEN
    }

    // How many failures before we open the circuit
    private static final int FAILURE_THRESHOLD = 5;

    // How long to wait before trying again (30 seconds)
    private static final long TIMEOUT_MS = 30_000;

    // Thread-safe counters — important because multiple requests
    // can hit this simultaneously
    private final AtomicInteger failureCount = new AtomicInteger(0);
    private final AtomicReference<CircuitState> state =
        new AtomicReference<>(CircuitState.CLOSED);
    private final AtomicLong openedAt = new AtomicLong(0);

    // Call this before processing any payment
    public void checkState() {
        CircuitState current = state.get();

        if (current == CircuitState.OPEN) {
            long elapsed = System.currentTimeMillis() - openedAt.get();

            if (elapsed >= TIMEOUT_MS) {
                // Enough time passed — try half open
                state.compareAndSet(CircuitState.OPEN, CircuitState.HALF_OPEN);
                log.info("Circuit moved to HALF_OPEN — testing recovery");
            } else {
                long remaining = (TIMEOUT_MS - elapsed) / 1000;
                log.warn("Circuit is OPEN. Retry in {}s", remaining);
                throw new CircuitOpenException(
                    "Payment service temporarily unavailable. Try again in "
                    + remaining + " seconds.");
            }
        }
    }

    // Call this when a payment succeeds
    public void recordSuccess() {
        failureCount.set(0);
        state.set(CircuitState.CLOSED);
        log.info("Circuit CLOSED — system healthy");
    }

    // Call this when a payment fails
    public void recordFailure() {
        int failures = failureCount.incrementAndGet();
        log.warn("Payment failure recorded. Count={}/{}", failures, FAILURE_THRESHOLD);

        if (failures >= FAILURE_THRESHOLD) {
            state.set(CircuitState.OPEN);
            openedAt.set(System.currentTimeMillis());
            log.error("Circuit OPENED after {} failures. Blocking requests for {}ms",
                failures, TIMEOUT_MS);
        }
    }

    public CircuitState getState() {
        return state.get();
    }

    public int getFailureCount() {
        return failureCount.get();
    }
}