package com.finance.transaction.dto;

public class ErrorResponse {
    public String message;
    
    public ErrorResponse() {
    }
    
    public ErrorResponse(String message) {
        this.message = message;
    }
}