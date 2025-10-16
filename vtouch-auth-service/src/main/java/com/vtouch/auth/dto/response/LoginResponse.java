package com.vtouch.auth.dto.response;

import com.vtouch.auth.dto.UserDTO;

public record LoginResponse(
    String accessToken,
    String refreshToken,
    Long expiresIn,
    UserDTO user
) {}
