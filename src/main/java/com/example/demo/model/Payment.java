package com.example.demo.model;

import com.example.demo.enums.PaymentStatus;
import com.example.demo.enums.PaymentType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Idempotency key — unique ID sent by client
    // Prevents duplicate payments from network retries
    // Same key = same request = don't process twice
    @Column(unique = true, length = 100)
    private String idempotencyKey;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_account_id")
    private Account fromAccount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_account_id")
    private Account toAccount;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status;

    @Column(nullable = true)
private String failureReason;

@Column(nullable = true)
private String description;

    @Column(nullable = false, columnDefinition = "INT DEFAULT 0")
    private Integer retryCount=0;

    @CreationTimestamp
    @Column(updatable = false)

    private LocalDateTime createdAt;
    @Column(nullable = true)
    private LocalDateTime processedAt;
}
