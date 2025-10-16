package com.venhancer.vmerge.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "User statistics summary")
public record UserStatistics(
    @Schema(description = "Total number of users", example = "100")
    long totalUsers,
    
    @Schema(description = "Number of active users", example = "80")
    long activeUsers,
    
    @Schema(description = "Number of inactive users", example = "15")
    long inactiveUsers,
    
    @Schema(description = "Number of suspended users", example = "3")
    long suspendedUsers,
    
    @Schema(description = "Number of deleted users", example = "2")
    long deletedUsers,
    
    @Schema(description = "Number of admin users", example = "5")
    long adminUsers,
    
    @Schema(description = "Number of moderator users", example = "10")
    long moderatorUsers,
    
    @Schema(description = "Number of regular users", example = "85")
    long regularUsers
) {} 