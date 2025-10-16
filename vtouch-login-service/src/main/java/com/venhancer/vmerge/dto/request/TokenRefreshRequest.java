package com.venhancer.vmerge.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Token refresh request")
public record TokenRefreshRequest(
        @Schema(description = "JWT refresh token", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...", required = true)
        @NotBlank(message = "Refresh token is required")
        String refreshToken
) {
} 