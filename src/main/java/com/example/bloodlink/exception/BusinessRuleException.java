package com.example.bloodlink.exception;

// thrown when the data is fine but the action is not allowed
// (donor donated too recently, request already closed, not enough stock ...)
public class BusinessRuleException extends RuntimeException {

    public BusinessRuleException(String message) {
        super(message);
    }
}
