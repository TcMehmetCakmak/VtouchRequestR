package com.venhancer.vmerge.dto.request;

import com.venhancer.vmerge.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

/**
 * Request record for updating an existing user
 */
@Schema(description = "Update User Request")
public record UpdateUserRequest(
    
    @Schema(description = "Username", example = "john_doe")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    String username,
    
    @Schema(description = "Email address", example = "john.doe@example.com")
    @Email(message = "Email format is invalid")
    @Size(max = 100, message = "Email must be less than 100 characters")
    String email,
    
    @Schema(description = "First name", example = "John")
    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
    String firstName,
    
    @Schema(description = "Last name", example = "Doe")
    @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
    String lastName,
    
    @Schema(description = "Phone number", example = "+1234567890")
    @Size(max = 20, message = "Phone number must be less than 20 characters")
    String phoneNumber,
    
    @Schema(description = "User role", example = "USER")
    User.UserRole role,
    
    @Schema(description = "User status", example = "ACTIVE")
    User.UserStatus status
) {
    
    /**
     * Apply updates to existing user entity
     * @param user Existing user entity
     * @return Updated user entity
     */
    public User applyTo(User user) {
        if (username != null) {
            user.setUsername(username);
        }
        if (email != null) {
            user.setEmail(email);
        }
        if (firstName != null) {
            user.setFirstName(firstName);
        }
        if (lastName != null) {
            user.setLastName(lastName);
        }
        if (phoneNumber != null) {
            user.setPhoneNumber(phoneNumber);
        }
        if (role != null) {
            user.setRole(role);
        }
        if (status != null) {
            user.setStatus(status);
        }
        return user;
    }
    
    /**
     * Check if request has any updates
     * @return true if at least one field is provided
     */
    public boolean hasUpdates() {
        return username != null || email != null || firstName != null || 
               lastName != null || phoneNumber != null || role != null || status != null;
    }
    
    /**
     * Get full name from first and last name (if both provided)
     * @return Full name or null if either name is missing
     */
    public String getFullName() {
        return (firstName != null && lastName != null) ? firstName + " " + lastName : null;
    }
} 