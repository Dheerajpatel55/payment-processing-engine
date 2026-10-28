package com.example.demo.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.example.demo.model.Account;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AccountResponse {
    private Long id;
    private String name;
    private String email;
    private String phone;
    private BigDecimal balance;
    private LocalDateTime createdAt;

    public AccountResponse(Account account) {
        this.id = account.getId();
        this.name = account.getName();
        this.email = account.getEmail();
        this.phone = account.getPhone();
        this.balance = account.getBalance();
        this.createdAt = account.getCreatedAt();
    }
}
