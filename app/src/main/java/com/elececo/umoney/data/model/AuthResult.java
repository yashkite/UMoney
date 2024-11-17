package com.elececo.umoney.data.model;

public class AuthResult {
    private final boolean success;
    private final String errorMessage;
    
    public AuthResult(boolean success, String errorMessage) {
        this.success = success;
        this.errorMessage = errorMessage;
    }
    
    public boolean isSuccess() {
        return success;
    }
    
    public String getErrorMessage() {
        return errorMessage;
    }
} 