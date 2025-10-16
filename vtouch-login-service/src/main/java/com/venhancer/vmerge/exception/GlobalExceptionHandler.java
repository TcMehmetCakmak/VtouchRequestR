package com.venhancer.vmerge.exception;

import com.venhancer.vmerge.dto.response.ApiResponse;
import com.venhancer.vmerge.dto.response.ErrorDetail;
import com.venhancer.vmerge.service.MessageService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestControllerAdvice(basePackages = "com.example.templatejava.controller")
public class GlobalExceptionHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    
    @Autowired
    private MessageService messageService;
    
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Object>> handleBusinessException(BusinessException ex, HttpServletRequest request) {
        logger.warn("Business exception: {}", ex.getMessage(), ex);
        
        String localizedMessage = messageService.getMessageWithDefault(ex.getErrorCode(), ex.getMessage());
        List<ErrorDetail> errors = List.of(ErrorDetail.global(ex.getErrorCode(), localizedMessage));
        
        ApiResponse<Object> response = ApiResponse.error(localizedMessage, errors).withPath(request.getRequestURI());
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
    
    @ExceptionHandler(SystemException.class)
    public ResponseEntity<ApiResponse<Object>> handleSystemException(SystemException ex, HttpServletRequest request) {
        String errorId = UUID.randomUUID().toString();
        logger.error("System exception [{}]: {}", errorId, ex.getMessage(), ex);
        
        String localizedMessage = messageService.getMessageWithDefault("SYSTEM_ERROR", "An internal system error occurred");
        List<ErrorDetail> errors = List.of(ErrorDetail.global(ex.getErrorCode(), localizedMessage));
        
        ApiResponse<Object> response = ApiResponse.error(localizedMessage, errors)
                .withPath(request.getRequestURI());
        response.addProperty("errorId", errorId);
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
    
    @ExceptionHandler(ThirdPartyServiceException.class)
    public ResponseEntity<ApiResponse<Object>> handleThirdPartyServiceException(ThirdPartyServiceException ex, HttpServletRequest request) {
        logger.error("Third-party service exception: {}", ex.getMessage(), ex);
        
        String localizedMessage = messageService.getMessageWithDefault(ex.getErrorCode(), ex.getMessage());
        List<ErrorDetail> errors = List.of(ErrorDetail.global(ex.getErrorCode(), localizedMessage));
        
        ApiResponse<Object> response = ApiResponse.error(localizedMessage, errors).withPath(request.getRequestURI());
        
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> handleValidationException(MethodArgumentNotValidException ex, HttpServletRequest request) {
        logger.warn("Validation exception: {}", ex.getMessage());
        
        List<ErrorDetail> errors = new ArrayList<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            String localizedMessage = messageService.getMessageWithDefault(error.getCode(), error.getDefaultMessage());
            errors.add(ErrorDetail.of(error.getField(), error.getCode(), localizedMessage, error.getRejectedValue()));
        }
        
        String message = messageService.getMessageWithDefault("VALIDATION_ERROR", "Validation failed");
        ApiResponse<Object> response = ApiResponse.error(message, errors).withPath(request.getRequestURI());
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
    
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Object>> handleConstraintViolationException(ConstraintViolationException ex, HttpServletRequest request) {
        logger.warn("Constraint violation exception: {}", ex.getMessage());
        
        List<ErrorDetail> errors = new ArrayList<>();
        for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
            String fieldName = violation.getPropertyPath().toString();
            String message = violation.getMessage();
            String localizedMessage = messageService.getMessageWithDefault("CONSTRAINT_VIOLATION", message);
            errors.add(ErrorDetail.of(fieldName, "CONSTRAINT_VIOLATION", localizedMessage, violation.getInvalidValue()));
        }
        
        String message = messageService.getMessageWithDefault("VALIDATION_ERROR", "Validation failed");
        ApiResponse<Object> response = ApiResponse.error(message, errors).withPath(request.getRequestURI());
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
    
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Object>> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex, HttpServletRequest request) {
        logger.warn("HTTP message not readable exception: {}", ex.getMessage());
        
        String message = messageService.getMessageWithDefault("INVALID_REQUEST_BODY", "Invalid request body");
        List<ErrorDetail> errors = List.of(ErrorDetail.global("INVALID_REQUEST_BODY", message));
        
        ApiResponse<Object> response = ApiResponse.error(message, errors).withPath(request.getRequestURI());
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
    
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<Object>> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex, HttpServletRequest request) {
        logger.warn("Method argument type mismatch exception: {}", ex.getMessage());
        
        String message = messageService.getMessageWithDefault("INVALID_PARAMETER_TYPE", 
                String.format("Invalid parameter type for '%s'", ex.getName()));
        List<ErrorDetail> errors = List.of(ErrorDetail.of(ex.getName(), "INVALID_PARAMETER_TYPE", message, ex.getValue()));
        
        ApiResponse<Object> response = ApiResponse.error(message, errors).withPath(request.getRequestURI());
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
    
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleNoHandlerFoundException(NoHandlerFoundException ex, HttpServletRequest request) {
        logger.warn("No handler found exception: {}", ex.getMessage());
        
        String message = messageService.getMessageWithDefault("ENDPOINT_NOT_FOUND", "Endpoint not found");
        List<ErrorDetail> errors = List.of(ErrorDetail.global("ENDPOINT_NOT_FOUND", message));
        
        ApiResponse<Object> response = ApiResponse.error(message, errors).withPath(request.getRequestURI());
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }
    
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse<Object>> handleDataIntegrityViolationException(DataIntegrityViolationException ex, HttpServletRequest request) {
        logger.error("Data integrity violation exception: {}", ex.getMessage(), ex);
        
        String message = messageService.getMessageWithDefault("DATA_INTEGRITY_ERROR", "Data integrity violation");
        List<ErrorDetail> errors = List.of(ErrorDetail.global("DATA_INTEGRITY_ERROR", message));
        
        ApiResponse<Object> response = ApiResponse.error(message, errors).withPath(request.getRequestURI());
        
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleGenericException(Exception ex, HttpServletRequest request) {
        String errorId = UUID.randomUUID().toString();
        logger.error("Unexpected exception [{}]: {}", errorId, ex.getMessage(), ex);
        
        String message = messageService.getMessageWithDefault("INTERNAL_SERVER_ERROR", "An unexpected error occurred");
        List<ErrorDetail> errors = List.of(ErrorDetail.global("INTERNAL_SERVER_ERROR", message));
        
        ApiResponse<Object> response = ApiResponse.error(message, errors)
                .withPath(request.getRequestURI());
        response.addProperty("errorId", errorId);
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
} 