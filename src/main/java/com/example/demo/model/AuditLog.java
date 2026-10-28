package com.example.demo.model;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Table(name = "audit_logs")
@Builder
public class AuditLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //entity was affected - payment or account
    @Column(nullable = false)
    private String entityType;

    // Id of entity
    @Column(nullable = false)
    private Long entityId;

    //what happened payment success or payment initiated
    @Column(nullable = false)
    private String action;

    // JSON snapshot of state before the action
    @Column(columnDefinition = "TEXT")
    private String previousState;

    // JSON snapshot of state after the action
    @Column(columnDefinition = "TEXT")
    private String newState;

    // Who triggered it
    private String performedBy;

    // Extra info — IP address, device etc
    private String metadata;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime timestamp;
}
