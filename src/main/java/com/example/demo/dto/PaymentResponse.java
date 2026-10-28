package com.example.demo.dto;

import com.example.demo.enums.PaymentStatus;
import com.example.demo.model.Payment;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PaymentResponse {

    private Long id;
    private String idempotencyKey;
    private Long fromAccountId;
    private String fromAccountName;
    private Long toAccountId;
    private String toAccountName;
    private BigDecimal amount;
    private PaymentStatus status;
    private String description;
    private String failureReason;
    private Integer retryCount;
    private LocalDateTime createdAt;
    private LocalDateTime processedAt;

    public PaymentResponse(Payment payment) {
        this.id = payment.getId();
        this.idempotencyKey = payment.getIdempotencyKey();
        this.amount = payment.getAmount();
        this.status = payment.getStatus();
        this.description = payment.getDescription();
        this.failureReason = payment.getFailureReason();
        this.retryCount = payment.getRetryCount();
        this.createdAt = payment.getCreatedAt();
        this.processedAt = payment.getProcessedAt();

        if (payment.getFromAccount() != null) {
            this.fromAccountId = payment.getFromAccount().getId();
            this.fromAccountName = payment.getFromAccount().getName();
        }
        if (payment.getToAccount() != null) {
            this.toAccountId = payment.getToAccount().getId();
            this.toAccountName = payment.getToAccount().getName();
        }
    }
}