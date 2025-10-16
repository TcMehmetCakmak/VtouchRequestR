package com.venhancer.vmerge.service;

import com.venhancer.vmerge.client.AuthClient;
import com.venhancer.vmerge.dto.response.ApiResponse;
import com.venhancer.vmerge.dto.UserDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class TokenIntrospectionService {
    
    private static final Logger logger = LoggerFactory.getLogger(TokenIntrospectionService.class);
    
    private final AuthClient authClient;
    
    public TokenIntrospectionService(AuthClient authClient) {
        this.authClient = authClient;
    }
    
    /**
     * Token'ı introspect eder ve geçerli olup olmadığını kontrol eder
     */
    public boolean isTokenValid(String token) {
        try {
            logger.debug("Token introspection for token: {}", token.substring(0, Math.min(10, token.length())) + "...");
            
            ApiResponse<UserDTO> response = authClient.getCurrentUser("Bearer " + token);
            
            if (response.success()) {
                logger.debug("Token is valid for user: {}", response.data().username());
                return true;
            } else {
                logger.warn("Token validation failed: {}", response.message());
                return false;
            }
        } catch (Exception e) {
            logger.error("Error during token introspection", e);
            return false;
        }
    }
    
    /**
     * Token'dan kullanıcı bilgilerini çıkarır
     */
    public UserDTO extractUserFromToken(String token) {
        try {
            ApiResponse<UserDTO> response = authClient.getCurrentUser("Bearer " + token);
            
            if (response.success()) {
                return response.data();
            } else {
                logger.warn("Failed to extract user from token: {}", response.message());
                return null;
            }
        } catch (Exception e) {
            logger.error("Error extracting user from token", e);
            return null;
        }
    }
}
