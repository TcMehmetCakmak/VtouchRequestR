package com.venhancer.vmerge.dto.response;

import com.venhancer.vmerge.dto.UserDTO;

public record LoginResponse(
    String accessToken,
    String refreshToken,
    Long expiresIn,
    UserDTO user
) {}
