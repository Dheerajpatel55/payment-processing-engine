package com.example.demo.dto;

import lombok.Data;
import java.math.BigDecimal;
import jakarta.validation.constraints.*;


@Data
public class CreateAccountRequest {
    @NotBlank(message = "Name required")
    private String name;

    @NotBlank(message = "Email required")
    @Email(message="invalid mail format")
    private String email;

   @NotBlank(message = "Phone is required")
    @Size(min = 10, max = 10, message = "Phone must be 10 digits")
    private String phone;

    @NotNull(message="Initial Balance is required")
    @DecimalMin(value = "0.0",inclusive = true, message = "balance cant be negative")
    private BigDecimal initialBalance;
        
}
