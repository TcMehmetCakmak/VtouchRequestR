package com.venhancer.vmerge.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Token refresh response containing new access token")
public record TokenRefreshResponse(
        @Schema(description = "New JWT access token", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
        String accessToken,

        @Schema(description = "Token type", example = "Bearer")
        String tokenType,

        @Schema(description = "Access token expiration in seconds", example = "86400")
        Long expiresIn
) {
    public TokenRefreshResponse(String accessToken, Long expiresIn) {
        this(accessToken, "Bearer", expiresIn);
    }
} 