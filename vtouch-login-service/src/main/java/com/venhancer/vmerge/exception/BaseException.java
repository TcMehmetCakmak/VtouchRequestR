package com.venhancer.vmerge.exception;

import java.util.HashMap;
import java.util.Map;

public abstract class BaseException extends RuntimeException {
    
    private final String errorCode;
    private final Map<String, Object> properties;
    
    public BaseException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
        this.properties = new HashMap<>();
    }
    
    public BaseException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.properties = new HashMap<>();
    }
    
    public BaseException(String errorCode, String message, Map<String, Object> properties) {
        super(message);
        this.errorCode = errorCode;
        this.properties = properties != null ? new HashMap<>(properties) : new HashMap<>();
    }
    
    public BaseException(String errorCode, String message, Throwable cause, Map<String, Object> properties) {
        super(message, cause);
        this.errorCode = errorCode;
        this.properties = properties != null ? new HashMap<>(properties) : new HashMap<>();
    }
    
    public String getErrorCode() {
        return errorCode;
    }
    
    public Map<String, Object> getProperties() {
        return new HashMap<>(properties);
    }
    
    public BaseException addProperty(String key, Object value) {
        this.properties.put(key, value);
        return this;
    }
    
    public Object getProperty(String key) {
        return properties.get(key);
    }
    
    public boolean hasProperty(String key) {
        return properties.containsKey(key);
    }
    
    @Override
    public String toString() {
        return String.format("%s(errorCode='%s', message='%s', properties=%s)", 
                this.getClass().getSimpleName(), errorCode, getMessage(), properties);
    }
} 