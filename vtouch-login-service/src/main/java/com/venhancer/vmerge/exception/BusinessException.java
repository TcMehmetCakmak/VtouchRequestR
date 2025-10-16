package com.venhancer.vmerge.exception;

import java.util.Map;

/**
 * Business exception for handling business logic errors.
 * These are expected exceptions that occur during normal business operations.
 */
public class BusinessException extends BaseException {
    
    public BusinessException(String errorCode, String message) {
        super(errorCode, message);
    }
    
    public BusinessException(String errorCode, String message, Map<String, Object> properties) {
        super(errorCode, message, properties);
    }
    
    // Common business exceptions
    public static class ResourceNotFoundException extends BusinessException {
        public ResourceNotFoundException(String resourceType, String identifier) {
            super("RESOURCE_NOT_FOUND", 
                  String.format("%s with identifier '%s' not found", resourceType, identifier));
            addProperty("resourceType", resourceType);
            addProperty("identifier", identifier);
        }
    }
    
    public static class DuplicateResourceException extends BusinessException {
        public DuplicateResourceException(String resourceType, String field, String value) {
            super("DUPLICATE_RESOURCE", 
                  String.format("%s with %s '%s' already exists", resourceType, field, value));
            addProperty("resourceType", resourceType);
            addProperty("field", field);
            addProperty("value", value);
        }
    }
    
    public static class InvalidOperationException extends BusinessException {
        public InvalidOperationException(String operation, String reason) {
            super("INVALID_OPERATION", 
                  String.format("Operation '%s' is not valid: %s", operation, reason));
            addProperty("operation", operation);
            addProperty("reason", reason);
        }
    }
    
    public static class InsufficientPermissionException extends BusinessException {
        public InsufficientPermissionException(String operation, String requiredPermission) {
            super("INSUFFICIENT_PERMISSION", 
                  String.format("Insufficient permission to perform '%s'. Required: %s", operation, requiredPermission));
            addProperty("operation", operation);
            addProperty("requiredPermission", requiredPermission);
        }
    }
    
    public static class ValidationException extends BusinessException {
        public ValidationException(String field, String message) {
            super("VALIDATION_ERROR", message);
            addProperty("field", field);
        }
        
        public ValidationException(String message, Map<String, Object> validationErrors) {
            super("VALIDATION_ERROR", message, validationErrors);
        }
    }
} 