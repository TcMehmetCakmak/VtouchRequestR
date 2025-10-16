package com.venhancer.vmerge.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Error Detail Information")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorDetail {
    
    @Schema(description = "Error field name", example = "email")
    private String field;
    
    @Schema(description = "Error code", example = "VALIDATION_ERROR")
    private String code;
    
    @Schema(description = "Error message", example = "Email is required")
    private String message;
    
    @Schema(description = "Rejected value", example = "invalid-email")
    private Object rejectedValue;
    
    public ErrorDetail() {
    }
    
    public ErrorDetail(String field, String code, String message) {
        this.field = field;
        this.code = code;
        this.message = message;
    }
    
    public ErrorDetail(String field, String code, String message, Object rejectedValue) {
        this.field = field;
        this.code = code;
        this.message = message;
        this.rejectedValue = rejectedValue;
    }
    
    // Static factory methods
    public static ErrorDetail of(String field, String code, String message) {
        return new ErrorDetail(field, code, message);
    }
    
    public static ErrorDetail of(String field, String code, String message, Object rejectedValue) {
        return new ErrorDetail(field, code, message, rejectedValue);
    }
    
    public static ErrorDetail global(String code, String message) {
        return new ErrorDetail(null, code, message);
    }
    
    // Getters and setters
    public String getField() {
        return field;
    }
    
    public void setField(String field) {
        this.field = field;
    }
    
    public String getCode() {
        return code;
    }
    
    public void setCode(String code) {
        this.code = code;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public Object getRejectedValue() {
        return rejectedValue;
    }
    
    public void setRejectedValue(Object rejectedValue) {
        this.rejectedValue = rejectedValue;
    }
} 