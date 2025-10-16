package com.venhancer.vmerge.exception;

import java.util.Map;

/**
 * Third-party service exception for handling external service integration errors.
 * These exceptions occur when external services fail or return unexpected responses.
 */
public class ThirdPartyServiceException extends BaseException {
    
    public ThirdPartyServiceException(String errorCode, String message) {
        super(errorCode, message);
    }
    
    public ThirdPartyServiceException(String errorCode, String message, Throwable cause) {
        super(errorCode, message, cause);
    }
    
    public ThirdPartyServiceException(String errorCode, String message, Map<String, Object> properties) {
        super(errorCode, message, properties);
    }
    
    public ThirdPartyServiceException(String errorCode, String message, Throwable cause, Map<String, Object> properties) {
        super(errorCode, message, cause, properties);
    }
    
    // Common third-party service exceptions
    public static class ServiceUnavailableException extends ThirdPartyServiceException {
        public ServiceUnavailableException(String serviceName) {
            super("SERVICE_UNAVAILABLE", String.format("Service '%s' is currently unavailable", serviceName));
            addProperty("serviceName", serviceName);
        }
        
        public ServiceUnavailableException(String serviceName, Throwable cause) {
            super("SERVICE_UNAVAILABLE", String.format("Service '%s' is currently unavailable", serviceName), cause);
            addProperty("serviceName", serviceName);
        }
    }
    
    public static class ServiceTimeoutException extends ThirdPartyServiceException {
        public ServiceTimeoutException(String serviceName, long timeoutMs) {
            super("SERVICE_TIMEOUT", String.format("Service '%s' timed out after %d ms", serviceName, timeoutMs));
            addProperty("serviceName", serviceName);
            addProperty("timeoutMs", timeoutMs);
        }
    }
    
    public static class ServiceAuthenticationException extends ThirdPartyServiceException {
        public ServiceAuthenticationException(String serviceName, String reason) {
            super("SERVICE_AUTHENTICATION_ERROR", 
                  String.format("Authentication failed for service '%s': %s", serviceName, reason));
            addProperty("serviceName", serviceName);
            addProperty("reason", reason);
        }
    }
    
    public static class ServiceAuthorizationException extends ThirdPartyServiceException {
        public ServiceAuthorizationException(String serviceName, String operation) {
            super("SERVICE_AUTHORIZATION_ERROR", 
                  String.format("Authorization failed for operation '%s' on service '%s'", operation, serviceName));
            addProperty("serviceName", serviceName);
            addProperty("operation", operation);
        }
    }
    
    public static class InvalidServiceResponseException extends ThirdPartyServiceException {
        public InvalidServiceResponseException(String serviceName, String expectedFormat, String actualFormat) {
            super("INVALID_SERVICE_RESPONSE", 
                  String.format("Invalid response from service '%s'. Expected: %s, Actual: %s", 
                                serviceName, expectedFormat, actualFormat));
            addProperty("serviceName", serviceName);
            addProperty("expectedFormat", expectedFormat);
            addProperty("actualFormat", actualFormat);
        }
    }
    
    public static class ServiceRateLimitException extends ThirdPartyServiceException {
        public ServiceRateLimitException(String serviceName, String retryAfter) {
            super("SERVICE_RATE_LIMIT", 
                  String.format("Rate limit exceeded for service '%s'. Retry after: %s", serviceName, retryAfter));
            addProperty("serviceName", serviceName);
            addProperty("retryAfter", retryAfter);
        }
    }
    
    public static class ServiceBadRequestException extends ThirdPartyServiceException {
        public ServiceBadRequestException(String serviceName, String reason) {
            super("SERVICE_BAD_REQUEST", 
                  String.format("Bad request to service '%s': %s", serviceName, reason));
            addProperty("serviceName", serviceName);
            addProperty("reason", reason);
        }
    }
    
    public static class ServiceInternalErrorException extends ThirdPartyServiceException {
        public ServiceInternalErrorException(String serviceName, int statusCode, String errorMessage) {
            super("SERVICE_INTERNAL_ERROR", 
                  String.format("Internal error in service '%s' (Status: %d): %s", serviceName, statusCode, errorMessage));
            addProperty("serviceName", serviceName);
            addProperty("statusCode", statusCode);
            addProperty("errorMessage", errorMessage);
        }
    }
} 