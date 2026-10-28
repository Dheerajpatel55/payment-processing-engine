package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.enums.PaymentStatus;
import com.example.demo.model.Payment;

import java.util.Optional;
import java.util.List;


@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long>{
        // Core idempotency check
    // Before processing any payment, we check if this key exists
    Optional<Payment> findByIdempotencyKey(String idempotencyKey);

    //get  all payment for a specific sender
    List<Payment> findByFromAccountIdOrderByCreatedAtDesc(Long accountId);

    //get all payment for a specific receiver
    List<Payment> findByToAccountIdOrderByCreatedAtDesc(long accountId);

    //get all pending payments - used by queue processor
    List<Payment> findByStatusOrderByCreatedAtAsc(PaymentStatus status);  

}
