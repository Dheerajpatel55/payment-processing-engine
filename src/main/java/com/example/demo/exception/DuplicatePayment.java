package com.example.demo.exception;

public class DuplicatePayment extends RuntimeException {
    public DuplicatePayment(String message){
        super(message);
    }
}
