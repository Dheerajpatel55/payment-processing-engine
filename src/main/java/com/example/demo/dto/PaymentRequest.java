package com.example.demo.dto;


import java.math.BigDecimal;

import jakarta.validation.constraints.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
@Data

public class PaymentRequest {
    @NotBlank(message = "Idempotency key is required")
    private String idempotencyKey;

    @NotNull(message = "Sender account ID is required")
    private Long fromAccountId;

    @NotNull(message = "Recevier Account ID is required")
    private Long toAccountId;

    @NotNull(message = "Enter amount that yu need to transfer")
    @DecimalMin(value = "1.00", message = "minimum amount is ₹1.00")
    @DecimalMax(value="100000.00",message="maximum transfer can be upto ₹1Lac")
    private BigDecimal amount;

    @NotBlank(message = "Description is required")
    @Size(max = 255, message = "Description is too long")
    private String description;
    
}
