package com.venhancer.vmerge.exception;

import java.util.Map;

/**
 * System exception for handling internal system errors.
 * These are unexpected exceptions that occur due to system failures.
 */
public class SystemException extends BaseException {
    
    public SystemException(String errorCode, String message) {
        super(errorCode, message);
    }
    
    public SystemException(String errorCode, String message, Throwable cause) {
        super(errorCode, message, cause);
    }
    
    public SystemException(String errorCode, String message, Map<String, Object> properties) {
        super(errorCode, message, properties);
    }
    
    public SystemException(String errorCode, String message, Throwable cause, Map<String, Object> properties) {
        super(errorCode, message, cause, properties);
    }
    
    // Common system exceptions
    public static class DatabaseException extends SystemException {
        public DatabaseException(String message, Throwable cause) {
            super("DATABASE_ERROR", "Database operation failed: " + message, cause);
        }
        
        public DatabaseException(String operation, String message, Throwable cause) {
            super("DATABASE_ERROR", String.format("Database %s failed: %s", operation, message), cause);
            addProperty("operation", operation);
        }
    }
    
    public static class ConfigurationException extends SystemException {
        public ConfigurationException(String configKey, String message) {
            super("CONFIGURATION_ERROR", String.format("Configuration error for '%s': %s", configKey, message));
            addProperty("configKey", configKey);
        }
    }
    
    public static class FileSystemException extends SystemException {
        public FileSystemException(String operation, String path, Throwable cause) {
            super("FILESYSTEM_ERROR", String.format("File system %s failed for path '%s'", operation, path), cause);
            addProperty("operation", operation);
            addProperty("path", path);
        }
    }
    
    public static class SecurityException extends SystemException {
        public SecurityException(String operation, String reason) {
            super("SECURITY_ERROR", String.format("Security violation during '%s': %s", operation, reason));
            addProperty("operation", operation);
            addProperty("reason", reason);
        }
    }
    
    public static class ConcurrencyException extends SystemException {
        public ConcurrencyException(String resource, String operation) {
            super("CONCURRENCY_ERROR", String.format("Concurrency conflict for resource '%s' during '%s'", resource, operation));
            addProperty("resource", resource);
            addProperty("operation", operation);
        }
    }
    
    public static class InternalServerException extends SystemException {
        public InternalServerException(String message) {
            super("INTERNAL_SERVER_ERROR", "Internal server error: " + message);
        }
        
        public InternalServerException(String message, Throwable cause) {
            super("INTERNAL_SERVER_ERROR", "Internal server error: " + message, cause);
        }
    }
} 