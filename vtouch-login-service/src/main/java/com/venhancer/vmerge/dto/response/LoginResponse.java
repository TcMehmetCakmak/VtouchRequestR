package com.venhancer.vmerge.dto.response;

import com.venhancer.vmerge.dto.UserDTO;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Login response containing JWT tokens and user information")
public record LoginResponse(
        @Schema(description = "JWT access token", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
        String accessToken,

        @Schema(description = "JWT refresh token", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
        String refreshToken,

        @Schema(description = "Token type", example = "Bearer")
        String tokenType,

        @Schema(description = "Access token expiration in seconds", example = "86400")
        Long expiresIn,

        @Schema(description = "Authenticated user information")
        UserDTO user
) {
    public LoginResponse(String accessToken, String refreshToken, Long expiresIn, UserDTO user) {
        this(accessToken, refreshToken, "Bearer", expiresIn, user);
    }
} 