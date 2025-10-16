package com.venhancer.vmerge.service;

import com.venhancer.vmerge.client.AuthClient;
import com.venhancer.vmerge.dto.request.LoginRequest;
import com.venhancer.vmerge.dto.request.RegisterRequest;
import com.venhancer.vmerge.dto.request.TokenRefreshRequest;
import com.venhancer.vmerge.dto.response.ApiResponse;
import com.venhancer.vmerge.dto.response.LoginResponse;
import com.venhancer.vmerge.dto.response.TokenRefreshResponse;
import com.venhancer.vmerge.dto.UserDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    
    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);
    
    private final AuthClient authClient;
    private final LoginLogService loginLogService;
    
    public AuthService(AuthClient authClient, LoginLogService loginLogService) {
        this.authClient = authClient;
        this.loginLogService = loginLogService;
    }
    
    public ApiResponse<LoginResponse> login(LoginRequest request) {
        logger.info("Login attempt for user: {}", request.username());
        
        try {
            ApiResponse<LoginResponse> response = authClient.login(request);
            if (response.success()) {
                logger.info("Login successful for user: {}", request.username());
            } else {
                logger.warn("Login failed for user: {} - {}", request.username(), response.message());
            }
            return response;
        } catch (Exception e) {
            logger.error("Error during login for user: {}", request.username(), e);
            return ApiResponse.error("Login service unavailable: " + e.getMessage());
        }
    }
    
    public ApiResponse<UserDTO> register(RegisterRequest request) {
        logger.info("Registration attempt for user: {}", request.username());
        
        try {
            ApiResponse<UserDTO> response = authClient.register(request);
            if (response.success()) {
                logger.info("Registration successful for user: {}", request.username());
            } else {
                logger.warn("Registration failed for user: {} - {}", request.username(), response.message());
            }
            return response;
        } catch (Exception e) {
            logger.error("Error during registration for user: {}", request.username(), e);
            return ApiResponse.error("Registration service unavailable: " + e.getMessage());
        }
    }
    
    public ApiResponse<TokenRefreshResponse> refreshToken(TokenRefreshRequest request) {
        logger.info("Token refresh attempt");
        
        try {
            ApiResponse<TokenRefreshResponse> response = authClient.refreshToken(request);
            if (response.success()) {
                logger.info("Token refresh successful");
            } else {
                logger.warn("Token refresh failed: {}", response.message());
            }
            return response;
        } catch (Exception e) {
            logger.error("Error during token refresh", e);
            return ApiResponse.error("Token refresh service unavailable: " + e.getMessage());
        }
    }
    
    public ApiResponse<UserDTO> getCurrentUser(String token) {
        logger.debug("Getting current user information");
        
        try {
            String authHeader = "Bearer " + token;
            ApiResponse<UserDTO> response = authClient.getCurrentUser(authHeader);
            if (response.success()) {
                logger.debug("User information retrieved successfully");
            } else {
                logger.warn("Failed to get user information: {}", response.message());
            }
            return response;
        } catch (Exception e) {
            logger.error("Error getting current user information", e);
            return ApiResponse.error("User service unavailable: " + e.getMessage());
        }
    }
    
    public ApiResponse<Void> logout(String token) {
        logger.info("Logout attempt");
        
        try {
            String authHeader = "Bearer " + token;
            ApiResponse<Void> response = authClient.logout(authHeader);
            if (response.success()) {
                logger.info("Logout successful");
            } else {
                logger.warn("Logout failed: {}", response.message());
            }
            return response;
        } catch (Exception e) {
            logger.error("Error during logout", e);
            return ApiResponse.error("Logout service unavailable: " + e.getMessage());
        }
    }
    
    public ApiResponse<UserDTO> introspectToken(String token) {
        logger.debug("Token introspection attempt");
        
        try {
            String authHeader = "Bearer " + token;
            ApiResponse<UserDTO> response = authClient.introspectToken(authHeader);
            
            if (response.success()) {
                logger.debug("Token introspection successful");
            } else {
                logger.warn("Token introspection failed: {}", response.message());
            }
            return response;
        } catch (Exception e) {
            logger.error("Error during token introspection", e);
            return ApiResponse.error("Token introspection service unavailable: " + e.getMessage());
        }
    }
}
