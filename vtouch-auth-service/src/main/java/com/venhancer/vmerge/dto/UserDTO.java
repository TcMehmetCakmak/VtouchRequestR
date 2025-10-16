package com.venhancer.vmerge.dto;

import java.time.LocalDateTime;

public record UserDTO(
    Long id,
    String username,
    String email,
    String firstName,
    String lastName,
    boolean enabled,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}
