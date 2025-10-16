package com.venhancer.vmerge.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "Generic API Response")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    
    @Schema(description = "Response status", example = "true")
    private boolean success;
    
    @Schema(description = "Response message", example = "Operation completed successfully")
    private String message;
    
    @Schema(description = "Response data")
    private T data;
    
    @Schema(description = "Error details")
    private List<ErrorDetail> errors;
    
    @Schema(description = "Response timestamp", example = "2024-01-01T10:00:00")
    private LocalDateTime timestamp;
    
    @Schema(description = "Request path", example = "/api/users")
    private String path;
    
    public ApiResponse() {
        this.timestamp = LocalDateTime.now();
    }
    
    public ApiResponse(boolean success, String message) {
        this();
        this.success = success;
        this.message = message;
    }
    
    public ApiResponse(boolean success, String message, T data) {
        this(success, message);
        this.data = data;
    }
    
    public ApiResponse(boolean success, String message, List<ErrorDetail> errors) {
        this(success, message);
        this.errors = errors;
    }
    
    // Static factory methods for success responses
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, "Operation completed successfully", data);
    }
    
    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(true, message, data);
    }
    
    public static <T> ApiResponse<T> success(String message) {
        return new ApiResponse<>(true, message);
    }
    
    // Static factory methods for error responses
    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(false, message);
    }
    
    public static <T> ApiResponse<T> error(String message, List<ErrorDetail> errors) {
        return new ApiResponse<>(false, message, errors);
    }
    
    // Builder pattern
    public static <T> ApiResponseBuilder<T> builder() {
        return new ApiResponseBuilder<>();
    }
    
    public ApiResponse<T> withPath(String path) {
        this.path = path;
        return this;
    }
    
    // Method to add custom properties (for storing additional metadata)
    private java.util.Map<String, Object> additionalProperties = new java.util.HashMap<>();
    
    public ApiResponse<T> addProperty(String key, Object value) {
        this.additionalProperties.put(key, value);
        return this;
    }
    
    public Object getProperty(String key) {
        return additionalProperties.get(key);
    }
    
    public java.util.Map<String, Object> getAdditionalProperties() {
        return additionalProperties;
    }
    
    // Getters and setters
    public boolean isSuccess() {
        return success;
    }
    
    public void setSuccess(boolean success) {
        this.success = success;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public T getData() {
        return data;
    }
    
    public void setData(T data) {
        this.data = data;
    }
    
    public List<ErrorDetail> getErrors() {
        return errors;
    }
    
    public void setErrors(List<ErrorDetail> errors) {
        this.errors = errors;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
    
    public String getPath() {
        return path;
    }
    
    public void setPath(String path) {
        this.path = path;
    }
    
    public static class ApiResponseBuilder<T> {
        private boolean success;
        private String message;
        private T data;
        private List<ErrorDetail> errors;
        private String path;
        
        public ApiResponseBuilder<T> success(boolean success) {
            this.success = success;
            return this;
        }
        
        public ApiResponseBuilder<T> message(String message) {
            this.message = message;
            return this;
        }
        
        public ApiResponseBuilder<T> data(T data) {
            this.data = data;
            return this;
        }
        
        public ApiResponseBuilder<T> errors(List<ErrorDetail> errors) {
            this.errors = errors;
            return this;
        }
        
        public ApiResponseBuilder<T> path(String path) {
            this.path = path;
            return this;
        }
        
        public ApiResponse<T> build() {
            ApiResponse<T> response = new ApiResponse<>(success, message, data);
            response.setErrors(errors);
            response.setPath(path);
            return response;
        }
    }
} 