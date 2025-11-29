package com.finance.auth.dto;

public class RegisterRequest {
    public String username;
    public String password;
    public String role = "USER";
    
    public RegisterRequest() {
    }
    
    public RegisterRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }
    
    public RegisterRequest(String username, String password, String role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }
}