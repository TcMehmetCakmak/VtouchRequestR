package com.venhancer.vmerge.exception;

/**
 * Exception thrown when JWT token is expired
 */
public class TokenExpiredException extends AuthenticationException {
    
    public TokenExpiredException() {
        super("TOKEN_EXPIRED", "JWT token has expired");
    }
    
    public TokenExpiredException(String message) {
        super("TOKEN_EXPIRED", message);
    }
} 