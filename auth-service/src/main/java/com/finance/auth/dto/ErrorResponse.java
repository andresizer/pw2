package com.finance.auth.dto;

public class ErrorResponse {
    public String message;
    
    public ErrorResponse() {
    }
    
    public ErrorResponse(String message) {
        this.message = message;
    }
}