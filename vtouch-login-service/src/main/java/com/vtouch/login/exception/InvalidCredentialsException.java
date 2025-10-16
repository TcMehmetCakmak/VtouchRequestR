package com.vtouch.login.exception;

/**
 * Exception thrown when user provides invalid credentials
 */
public class InvalidCredentialsException extends AuthenticationException {
    
    public InvalidCredentialsException() {
        super("INVALID_CREDENTIALS", "Invalid username or password");
    }
    
    public InvalidCredentialsException(String message) {
        super("INVALID_CREDENTIALS", message);
    }
} 