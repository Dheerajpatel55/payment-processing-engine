package com.example.demo.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.dto.PaymentRequest;
import com.example.demo.dto.PaymentResponse;
import com.example.demo.enums.PaymentStatus;
import com.example.demo.enums.PaymentType;
import com.example.demo.exception.AccountNotFoundException;
import com.example.demo.exception.InsufficientBalanceException;
import com.example.demo.model.Account;
import com.example.demo.model.AuditLog;
import com.example.demo.model.Payment;
import com.example.demo.repository.AccountRepository;
import com.example.demo.repository.PaymentRepository;
import com.example.demo.repository.AuditLogsRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final AccountRepository accountRepository;
    private final IdempotencyService idempotencyService;
    private final AuditLogsRepository AuditLogsRepository;
    private final CircuitBreakerService circuitBreakerService;

    @Transactional
    public PaymentResponse initiatePayment(PaymentRequest request) {

        // ── STEP 1: IDEMPOTENCY CHECK ──────────────────────────────
        // If client retries same request, return original result
        // without processing again
        var existing = idempotencyService.findExistingPayment(
            request.getIdempotencyKey());
        if (existing.isPresent()) {
            log.info("Returning cached result for key={}",
                request.getIdempotencyKey());
            return new PaymentResponse(existing.get());
        }

        // ── STEP 2: CIRCUIT BREAKER CHECK ─────────────────────────
        // If too many failures happened recently, reject fast
        // instead of wasting time trying a broken system
        circuitBreakerService.checkState();

        // ── STEP 3: LOAD ACCOUNTS ──────────────────────────────────
        Account sender = accountRepository.findById(request.getFromAccountId())
            .orElseThrow(() -> new AccountNotFoundException(
                "Sender account not found: " + request.getFromAccountId()));

        Account receiver = accountRepository.findById(request.getToAccountId())
            .orElseThrow(() -> new AccountNotFoundException(
                "Receiver account not found: " + request.getToAccountId()));

        // ── STEP 4: VALIDATE ───────────────────────────────────────
        validatePayment(sender, receiver, request.getAmount());

        // ── STEP 5: CREATE PAYMENT RECORD (PENDING) ────────────────
        // Save payment as PENDING first
        // If anything fails after this, we have a record to retry
        Payment payment = createPendingPayment(request, sender, receiver);

        try {
            // ── STEP 6: EXECUTE TRANSFER ───────────────────────────
            // @Transactional ensures both debit + credit happen
            // atomically — if crash occurs, both rollback
            // @Version on Account prevents concurrent modification
            String senderBefore = "balance=" + sender.getBalance();

            sender.setBalance(sender.getBalance().subtract(request.getAmount()));
            receiver.setBalance(receiver.getBalance().add(request.getAmount()));

            accountRepository.save(sender);
            accountRepository.save(receiver);

            // ── STEP 7: MARK SUCCESS ───────────────────────────────
            payment.setStatus(PaymentStatus.SUCCESS);
            payment.setProcessedAt(LocalDateTime.now());
            paymentRepository.save(payment);

            // ── STEP 8: AUDIT LOG ──────────────────────────────────
            saveAuditLog(
                "PAYMENT", payment.getId(),
                "TRANSFER_SUCCESS",
                senderBefore,
                "balance=" + sender.getBalance(),
                "system"
            );

            // ── STEP 9: RECORD CIRCUIT BREAKER SUCCESS ─────────────
            circuitBreakerService.recordSuccess();

            log.info("Payment SUCCESS: id={}, amount={}, from={}, to={}",
                payment.getId(), request.getAmount(),
                sender.getId(), receiver.getId());

            return new PaymentResponse(payment);

        } catch (Exception e) {

            // ── ON FAILURE: MARK PAYMENT FAILED ───────────────────
            payment.setStatus(PaymentStatus.FAILED);
            payment.setFailureReason(e.getMessage());
            payment.setRetryCount(payment.getRetryCount() + 1);
            paymentRepository.save(payment);

            // Record failure in circuit breaker
            circuitBreakerService.recordFailure();

            saveAuditLog(
                "PAYMENT", payment.getId(),
                "TRANSFER_FAILED",
                null, e.getMessage(), "system"
            );

            log.error("Payment FAILED: id={}, reason={}", payment.getId(), e.getMessage());
            throw new RuntimeException("Payment failed: " + e.getMessage());
        }
    }

    private void validatePayment(Account sender, Account receiver, BigDecimal amount) {

        if (!sender.isActive()) {
            throw new RuntimeException("Sender account is inactive");
        }
        if (!receiver.isActive()) {
            throw new RuntimeException("Receiver account is inactive");
        }
        if (sender.getId().equals(receiver.getId())) {
            throw new RuntimeException("Cannot transfer to same account");
        }
        // BigDecimal comparison — never use == for BigDecimal
        if (sender.getBalance().compareTo(amount) < 0) {
            throw new InsufficientBalanceException(
                "Insufficient balance. Available: ₹" + sender.getBalance()
                + ", Requested: ₹" + amount);
        }
    }

    private Payment createPendingPayment(PaymentRequest request,
            Account sender, Account receiver) {

        Payment payment = new Payment();
        payment.setIdempotencyKey(request.getIdempotencyKey());
        payment.setFromAccount(sender);
        payment.setToAccount(receiver);
        payment.setAmount(request.getAmount());
        payment.setType(PaymentType.TRANSFER);
        payment.setStatus(PaymentStatus.PENDING);
        payment.setDescription(request.getDescription());
        payment.setRetryCount(0);

        return paymentRepository.save(payment);
    }

    private void saveAuditLog(String entityType, Long entityId,
            String action, String previousState,
            String newState, String performedBy) {

        AuditLog log = AuditLog.builder()
            .entityType(entityType)
            .entityId(entityId)
            .action(action)
            .previousState(previousState)
            .newState(newState)
            .performedBy(performedBy)
            .build();

        AuditLogsRepository.save(log);
    }

    @Transactional(readOnly = true)
    public PaymentResponse getPayment(Long id) {
        Payment payment = paymentRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Payment not found: " + id));
        return new PaymentResponse(payment);
    }

    @Transactional(readOnly = true)
    public List<PaymentResponse> getAccountPayments(Long accountId) {
        // Verify account exists first
        accountRepository.findById(accountId)
            .orElseThrow(() -> new AccountNotFoundException(
                "Account not found: " + accountId));

        List<Payment> sent = paymentRepository
            .findByFromAccountIdOrderByCreatedAtDesc(accountId);
        List<Payment> received = paymentRepository
            .findByToAccountIdOrderByCreatedAtDesc(accountId);

        // Merge both lists
        return java.util.stream.Stream
            .concat(sent.stream(), received.stream())
            .map(PaymentResponse::new)
            .toList();
    }

    @Transactional(readOnly = true)
    public CircuitBreakerService.CircuitState getCircuitState() {
        return circuitBreakerService.getState();
    }
}