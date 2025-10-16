package com.venhancer.vmerge.controller;

import com.venhancer.vmerge.dto.UserDTO;
import com.venhancer.vmerge.dto.request.LoginRequest;
import com.venhancer.vmerge.dto.request.RegisterRequest;
import com.venhancer.vmerge.dto.request.TokenRefreshRequest;
import com.venhancer.vmerge.dto.response.ApiResponse;
import com.venhancer.vmerge.dto.response.LoginResponse;
import com.venhancer.vmerge.dto.response.TokenRefreshResponse;
import com.venhancer.vmerge.entity.User;
import com.venhancer.vmerge.exception.InvalidCredentialsException;
import com.venhancer.vmerge.exception.InvalidTokenException;
import com.venhancer.vmerge.service.JwtService;
import com.venhancer.vmerge.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication", description = "Authentication and user management endpoints")
public class AuthController {
    
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    
    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtService jwtService;
    
    public AuthController(AuthenticationManager authenticationManager,
                         UserService userService,
                         JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.jwtService = jwtService;
    }
    
    @PostMapping("/login")
    @Operation(summary = "User login", description = "Authenticate user and return JWT tokens")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Login successful"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Invalid credentials"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request")
    })
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        logger.info("Login attempt for user: {}", request.username());
        
        try {
            // Authenticate user
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password())
            );
            
            User user = (User) authentication.getPrincipal();
            
            // Generate tokens
            String accessToken = jwtService.generateToken(user);
            String refreshToken = jwtService.generateRefreshToken(user);
            
            LoginResponse loginResponse = new LoginResponse(
                accessToken,
                refreshToken,
                jwtService.getJwtExpiration() / 1000, // Convert to seconds
                UserDTO.fromEntity(user)
            );
            
            logger.info("Login successful for user: {}", request.username());
            return ResponseEntity.ok(ApiResponse.success("Login successful", loginResponse));
            
        } catch (AuthenticationException e) {
            logger.warn("Login failed for user: {} - {}", request.username(), e.getMessage());
            throw new InvalidCredentialsException();
        }
    }
    
    @PostMapping("/register")
    @Operation(summary = "User registration", description = "Register a new user account")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Registration successful"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request or user already exists"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Username or email already exists")
    })
    public ResponseEntity<ApiResponse<UserDTO>> register(@Valid @RequestBody RegisterRequest request) {
        logger.info("Registration attempt for user: {}", request.username());
        
        ApiResponse<UserDTO> response = userService.registerUser(request);
        
        logger.info("Registration successful for user: {}", request.username());
        return ResponseEntity.status(201).body(response);
    }
    
    @PostMapping("/refresh")
    @Operation(summary = "Refresh access token", description = "Generate new access token using refresh token")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Token refresh successful"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Invalid or expired refresh token"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request")
    })
    public ResponseEntity<ApiResponse<TokenRefreshResponse>> refreshToken(@Valid @RequestBody TokenRefreshRequest request) {
        logger.info("Token refresh attempt");
        
        try {
            String refreshToken = request.refreshToken();
            
            // Extract username from refresh token
            String username = jwtService.extractUsername(refreshToken);
            
            // Load user details
            UserDetails userDetails = userService.loadUserByUsername(username);
            
            // Validate refresh token
            if (!jwtService.isTokenValid(refreshToken, userDetails)) {
                throw new InvalidTokenException("Invalid refresh token");
            }
            
            // Generate new access token
            String newAccessToken = jwtService.generateToken(userDetails);
            
            TokenRefreshResponse tokenResponse = new TokenRefreshResponse(
                newAccessToken,
                jwtService.getJwtExpiration() / 1000 // Convert to seconds
            );
            
            logger.info("Token refresh successful for user: {}", username);
            return ResponseEntity.ok(ApiResponse.success("Token refresh successful", tokenResponse));
            
        } catch (Exception e) {
            logger.warn("Token refresh failed: {}", e.getMessage());
            
            if (e instanceof InvalidTokenException) {
                throw e;
            } else {
                throw new InvalidTokenException("Invalid refresh token");
            }
        }
    }
    
    @GetMapping("/me")
    @Operation(summary = "Get current user", description = "Get information about the currently authenticated user")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "User information retrieved successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Authentication required")
    })
    public ResponseEntity<ApiResponse<UserDTO>> getCurrentUser(Authentication authentication) {
        logger.debug("Getting current user information");
        
        User user = (User) authentication.getPrincipal();
        UserDTO userDTO = UserDTO.fromEntity(user);
        
        return ResponseEntity.ok(ApiResponse.success("User information retrieved successfully", userDTO));
    }
    
    @PostMapping("/logout")
    @Operation(summary = "User logout", description = "Logout the current user (client-side token invalidation)")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Logout successful"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Authentication required")
    })
    public ResponseEntity<ApiResponse<Void>> logout(Authentication authentication) {
        logger.info("Logout for user: {}", authentication.getName());
        
        // Note: With stateless JWT, logout is typically handled on the client side
        // by removing the token from storage. For server-side token invalidation,
        // you would need to implement a token blacklist mechanism.
        
        return ResponseEntity.ok(ApiResponse.success("Logout successful"));
    }
} 