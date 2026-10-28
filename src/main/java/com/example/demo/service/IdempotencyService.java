package com.example.demo.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.demo.model.Payment;
import com.example.demo.repository.PaymentRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
@Service
@RequiredArgsConstructor
@Slf4j
public class IdempotencyService {
    private final PaymentRepository paymentRepository;

    // check payment was already processed
    public Optional<Payment> findExistingPayment(String idempotencyKey){
        Optional<Payment> exisitng = paymentRepository.findByIdempotencyKey(idempotencyKey);

    if(exisitng.isPresent()){
        log.warn("Duplicate payment detected. Key={}, status={}",idempotencyKey,exisitng.get().getStatus());
    }
    return exisitng;
    }

    public boolean isDuplicate(String idempotencyKey){
        return paymentRepository.findByIdempotencyKey(idempotencyKey).isPresent();
    }
}
