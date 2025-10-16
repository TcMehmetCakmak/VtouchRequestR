package com.venhancer.vmerge.controller;

import com.venhancer.vmerge.dto.UserDTO;
import com.venhancer.vmerge.dto.UserStatistics;
import com.venhancer.vmerge.dto.request.CreateUserRequest;
import com.venhancer.vmerge.dto.request.UpdateUserRequest;
import com.venhancer.vmerge.dto.response.ApiResponse;
import com.venhancer.vmerge.dto.response.PagedResponse;
import com.venhancer.vmerge.entity.User;
import com.venhancer.vmerge.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@Tag(name = "User Management", description = "APIs for managing users")
public class UserController {
    
    @Autowired
    private UserService userService;
    
    @PostMapping
    @Operation(summary = "Create a new user", description = "Creates a new user with the provided information")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "User created successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid input data"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "User already exists")
    })
    public ResponseEntity<ApiResponse<UserDTO>> createUser(
            @Valid @RequestBody CreateUserRequest request) {
        
        ApiResponse<UserDTO> response = userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @GetMapping("/{userId}")
    @Operation(summary = "Get user by ID", description = "Retrieves a user by their unique identifier")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "User retrieved successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<ApiResponse<UserDTO>> getUserById(
            @Parameter(description = "User ID", required = true)
            @PathVariable Long userId) {
        ApiResponse<UserDTO> response = userService.getUserById(userId);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/username/{username}")
    @Operation(summary = "Get user by username", description = "Retrieves a user by their username")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "User retrieved successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<ApiResponse<UserDTO>> getUserByUsername(
            @Parameter(description = "Username", required = true)
            @PathVariable String username) {
        ApiResponse<UserDTO> response = userService.getUserByUsername(username);
        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/{userId}")
    @Operation(summary = "Update user", description = "Updates an existing user with the provided information")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "User updated successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid input data"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "User not found"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Duplicate username or email")
    })
    public ResponseEntity<ApiResponse<UserDTO>> updateUser(
            @Parameter(description = "User ID", required = true)
            @PathVariable Long userId,
            @Valid @RequestBody UpdateUserRequest request) {
        ApiResponse<UserDTO> response = userService.updateUser(userId, request);
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{userId}")
    @Operation(summary = "Delete user", description = "Soft deletes a user by setting their status to DELETED")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "User deleted successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "User not found"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "User already deleted")
    })
    public ResponseEntity<ApiResponse<Void>> deleteUser(
            @Parameter(description = "User ID", required = true)
            @PathVariable Long userId) {
        ApiResponse<Void> response = userService.deleteUser(userId);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping
    @Operation(summary = "Get all users", description = "Retrieves all users with pagination and sorting")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Users retrieved successfully")
    })
    public ResponseEntity<ApiResponse<PagedResponse<UserDTO>>> getAllUsers(
            @Parameter(description = "Page number (0-based)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size", example = "10")
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort field", example = "createdAt")
            @RequestParam(defaultValue = "createdAt") String sort,
            @Parameter(description = "Sort direction", example = "desc")
            @RequestParam(defaultValue = "desc") String direction) {
        ApiResponse<PagedResponse<UserDTO>> response = userService.getAllUsers(page, size, sort, direction);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/search")
    @Operation(summary = "Search users", description = "Searches users by keyword, status, and role with pagination")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Search results retrieved successfully")
    })
    public ResponseEntity<ApiResponse<PagedResponse<UserDTO>>> searchUsers(
            @Parameter(description = "Search keyword", example = "john")
            @RequestParam(required = false) String keyword,
            @Parameter(description = "User status filter", example = "ACTIVE")
            @RequestParam(required = false) User.UserStatus status,
            @Parameter(description = "User role filter", example = "USER")
            @RequestParam(required = false) User.UserRole role,
            @Parameter(description = "Page number (0-based)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size", example = "10")
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort field", example = "createdAt")
            @RequestParam(defaultValue = "createdAt") String sort,
            @Parameter(description = "Sort direction", example = "desc")
            @RequestParam(defaultValue = "desc") String direction) {
        ApiResponse<PagedResponse<UserDTO>> response = userService.searchUsers(keyword, status, role, page, size, sort, direction);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/statistics")
    @Operation(summary = "Get user statistics", description = "Retrieves user statistics including counts by status and role")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Statistics retrieved successfully")
    })
    public ResponseEntity<ApiResponse<UserStatistics>> getUserStatistics() {
        ApiResponse<UserStatistics> response = userService.getUserStatistics();
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/recent")
    @Operation(summary = "Get recently created users", description = "Retrieves users created in the last 30 days")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Recent users retrieved successfully")
    })
    public ResponseEntity<ApiResponse<PagedResponse<UserDTO>>> getRecentlyCreatedUsers(
            @Parameter(description = "Page number (0-based)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size", example = "10")
            @RequestParam(defaultValue = "10") int size) {
        ApiResponse<PagedResponse<UserDTO>> response = userService.getRecentlyCreatedUsers(page, size);
        return ResponseEntity.ok(response);
    }
    
    @PatchMapping("/{userId}/status")
    @Operation(summary = "Change user status", description = "Changes the status of a user")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "User status changed successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "User not found"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid status change")
    })
    public ResponseEntity<ApiResponse<UserDTO>> changeUserStatus(
            @Parameter(description = "User ID", required = true)
            @PathVariable Long userId,
            @Parameter(description = "New status", required = true)
            @RequestParam User.UserStatus status) {
        ApiResponse<UserDTO> response = userService.changeUserStatus(userId, status);
        return ResponseEntity.ok(response);
    }
    
    @PatchMapping("/{userId}/role")
    @Operation(summary = "Change user role", description = "Changes the role of a user")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "User role changed successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "User not found"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid role change")
    })
    public ResponseEntity<ApiResponse<UserDTO>> changeUserRole(
            @Parameter(description = "User ID", required = true)
            @PathVariable Long userId,
            @Parameter(description = "New role", required = true)
            @RequestParam User.UserRole role) {
        
        ApiResponse<UserDTO> response = userService.changeUserRole(userId, role);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/by-status/{status}")
    @Operation(summary = "Get users by status", description = "Retrieves users with the specified status")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Users retrieved successfully")
    })
    public ResponseEntity<ApiResponse<PagedResponse<UserDTO>>> getUsersByStatus(
            @Parameter(description = "User status", required = true)
            @PathVariable User.UserStatus status,
            @Parameter(description = "Page number (0-based)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size", example = "10")
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort field", example = "createdAt")
            @RequestParam(defaultValue = "createdAt") String sort,
            @Parameter(description = "Sort direction", example = "desc")
            @RequestParam(defaultValue = "desc") String direction) {
        ApiResponse<PagedResponse<UserDTO>> response = userService.searchUsers(null, status, null, page, size, sort, direction);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/by-role/{role}")
    @Operation(summary = "Get users by role", description = "Retrieves users with the specified role")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Users retrieved successfully")
    })
    public ResponseEntity<ApiResponse<PagedResponse<UserDTO>>> getUsersByRole(
            @Parameter(description = "User role", required = true)
            @PathVariable User.UserRole role,
            @Parameter(description = "Page number (0-based)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size", example = "10")
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort field", example = "createdAt")
            @RequestParam(defaultValue = "createdAt") String sort,
            @Parameter(description = "Sort direction", example = "desc")
            @RequestParam(defaultValue = "desc") String direction) {
        ApiResponse<PagedResponse<UserDTO>> response = userService.searchUsers(null, null, role, page, size, sort, direction);
        return ResponseEntity.ok(response);
    }
} 