package com.venhancer.vmerge.dto.response;

public record TokenRefreshResponse(
    String accessToken,
    Long expiresIn
) {}
