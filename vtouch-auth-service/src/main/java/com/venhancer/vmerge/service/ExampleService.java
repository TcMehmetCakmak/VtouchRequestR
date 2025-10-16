package com.venhancer.vmerge.service;

import com.venhancer.vmerge.dto.response.ApiResponse;
import com.venhancer.vmerge.dto.UserDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ExampleService {
    
    private static final Logger logger = LoggerFactory.getLogger(ExampleService.class);
    
    private final AuthService authService;
    
    public ExampleService(AuthService authService) {
        this.authService = authService;
    }
    
    /**
     * Example method that requires authentication
     * This method demonstrates how to use the auth service to validate tokens
     */
    public ApiResponse<String> performAuthenticatedOperation(String token) {
        logger.info("Performing authenticated operation");
        
        // Validate token and get user information
        ApiResponse<UserDTO> userResponse = authService.getCurrentUser(token);
        
        if (!userResponse.success()) {
            logger.warn("Authentication failed: {}", userResponse.message());
            return ApiResponse.error("Authentication required: " + userResponse.message());
        }
        
        UserDTO user = userResponse.data();
        logger.info("User {} is performing authenticated operation", user.username());
        
        // Perform the actual business logic here
        String result = "Operation completed successfully for user: " + user.username();
        
        return ApiResponse.success("Operation completed successfully", result);
    }
    
    /**
     * Example method that demonstrates token refresh
     */
    public ApiResponse<String> performOperationWithTokenRefresh(String refreshToken) {
        logger.info("Performing operation with token refresh");
        
        // Refresh the access token
        var refreshRequest = new com.venhancer.vmerge.dto.request.TokenRefreshRequest(refreshToken);
        ApiResponse<com.venhancer.vmerge.dto.response.TokenRefreshResponse> refreshResponse = 
            authService.refreshToken(refreshRequest);
        
        if (!refreshResponse.success()) {
            logger.warn("Token refresh failed: {}", refreshResponse.message());
            return ApiResponse.error("Token refresh failed: " + refreshResponse.message());
        }
        
        String newAccessToken = refreshResponse.data().accessToken();
        logger.info("Token refreshed successfully");
        
        // Now perform the operation with the new token
        return performAuthenticatedOperation(newAccessToken);
    }
}
