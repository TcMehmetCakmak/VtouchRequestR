package com.venhancer.vmerge.exception;

/**
 * Exception thrown when JWT token is invalid or malformed
 */
public class InvalidTokenException extends AuthenticationException {
    
    public InvalidTokenException() {
        super("INVALID_TOKEN", "JWT token is invalid or malformed");
    }
    
    public InvalidTokenException(String message) {
        super("INVALID_TOKEN", message);
    }
} 