package com.vtouch.auth.dto.response;

public record TokenRefreshResponse(
    String accessToken,
    Long expiresIn
) {}
