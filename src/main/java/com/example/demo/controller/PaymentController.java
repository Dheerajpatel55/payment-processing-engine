package com.example.demo.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;

import com.example.demo.dto.PaymentRequest;
import com.example.demo.dto.PaymentResponse;
import com.example.demo.service.CircuitBreakerService;
import com.example.demo.service.PaymentService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("api/payments")
@RequiredArgsConstructor
@Slf4j
public class PaymentController {
    private final PaymentService paymentService;

    @PostMapping("/transfer")
    public ResponseEntity<PaymentResponse> transfer(
        @Valid @RequestBody PaymentRequest request){
            log.info("transfer request: from = {}, to = {}, amount = {}", 
            request.getFromAccountId(),
            request.getToAccountId(),
            request.getAmount());
        PaymentResponse response = paymentService.initiatePayment(request);
        return ResponseEntity.ok(response);
    }
//RequestParam
//get single payment by ID
    @GetMapping("{id}")
    public ResponseEntity<PaymentResponse> getPayment(@PathVariable Long id) {
        return ResponseEntity.ok(paymentService.getPayment(id));
    }
    //get all payment history of account

    @GetMapping("/account/{accountId}")
    public ResponseEntity<List<PaymentResponse>> getAccountPayments(
    @PathVariable Long accountId){
        return ResponseEntity.ok(paymentService.getAccountPayments(accountId));
    }

    //check circuit breaker
    @GetMapping("circuit-status")
    public ResponseEntity<Map<String, Object>> getCircuitStatus(){
        Map<String, Object> status = new HashMap<>();
        status.put("state", paymentService.getCircuitState());
        status.put("description", getCircuitDescription(
            paymentService.getCircuitState()));
        return ResponseEntity.ok(status);
    }
     private String getCircuitDescription(CircuitBreakerService.CircuitState state) {
        return switch (state) {
            case CLOSED -> "System healthy. All payments processing normally.";
            case OPEN -> "Too many failures detected. Payments temporarily blocked.";
            case HALF_OPEN -> "System recovering. Testing with limited requests.";
        };
    }
    
}
