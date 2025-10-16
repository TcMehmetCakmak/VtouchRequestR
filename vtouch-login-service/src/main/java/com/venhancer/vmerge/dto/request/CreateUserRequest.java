package com.venhancer.vmerge.dto.request;

import com.venhancer.vmerge.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request record for creating a new user
 */
@Schema(description = "Create User Request")
public record CreateUserRequest(
    
    @Schema(description = "Username", example = "john_doe", required = true)
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    String username,
    
    @Schema(description = "Email address", example = "john.doe@example.com", required = true)
    @NotBlank(message = "Email is required")
    @Email(message = "Email format is invalid")
    @Size(max = 100, message = "Email must be less than 100 characters")
    String email,
    
    @Schema(description = "Password", example = "password123", required = true)
    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    String password,
    
    @Schema(description = "First name", example = "John", required = true)
    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
    String firstName,
    
    @Schema(description = "Last name", example = "Doe", required = true)
    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
    String lastName,
    
    @Schema(description = "Phone number", example = "+1234567890")
    @Size(max = 20, message = "Phone number must be less than 20 characters")
    String phoneNumber,
    
    @Schema(description = "User role", example = "USER")
    User.UserRole role
) {
    
    /**
     * Convert request to User entity
     * @return User entity
     */
    public User toEntity() {
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setPhoneNumber(phoneNumber);
        user.setRole(role != null ? role : User.UserRole.USER);
        user.setStatus(User.UserStatus.ACTIVE);
        return user;
    }
    
    /**
     * Get full name from first and last name
     * @return Full name
     */
    public String getFullName() {
        return firstName + " " + lastName;
    }
} 