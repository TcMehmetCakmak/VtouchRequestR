package com.venhancer.vmerge.exception;

/**
 * Exception thrown when authentication fails
 */
public class AuthenticationException extends BusinessException {
    
    public AuthenticationException(String message) {
        super("AUTH_FAILED", message);
    }
    
    public AuthenticationException(String code, String message) {
        super(code, message);
    }
} 