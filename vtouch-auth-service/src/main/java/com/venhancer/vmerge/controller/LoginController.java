package com.venhancer.vmerge.controller;

import com.venhancer.vmerge.dto.request.LoginRequest;
import com.venhancer.vmerge.dto.request.RegisterRequest;
import com.venhancer.vmerge.dto.request.TokenRefreshRequest;
import com.venhancer.vmerge.dto.response.ApiResponse;
import com.venhancer.vmerge.dto.response.LoginResponse;
import com.venhancer.vmerge.dto.response.TokenRefreshResponse;
import com.venhancer.vmerge.dto.UserDTO;
import com.venhancer.vmerge.service.AuthService;
import com.venhancer.vmerge.service.LoginLogService;
// import io.swagger.v3.oas.annotations.Operation;
// import io.swagger.v3.oas.annotations.responses.ApiResponse;
// import io.swagger.v3.oas.annotations.responses.ApiResponses;
// import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/login")
// @Tag(name = "Login Service", description = "Login service endpoints that communicate with auth service")
public class LoginController {
    
    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);
    
    private final AuthService authService;
    private final LoginLogService loginLogService;
    
    public LoginController(AuthService authService, LoginLogService loginLogService) {
        this.authService = authService;
        this.loginLogService = loginLogService;
    }
    
    @PostMapping(value = "/authenticate", produces = "application/json", consumes = "application/json")
    // @Operation(summary = "User authentication", description = "Authenticate user through auth service")
    // @ApiResponses(value = {
    //     @ApiResponse(responseCode = "200", description = "Authentication successful"),
    //     @ApiResponse(responseCode = "401", description = "Invalid credentials"),
    //     @ApiResponse(responseCode = "400", description = "Invalid request")
    // })
    public ResponseEntity<ApiResponse<LoginResponse>> authenticate(@Valid @RequestBody LoginRequest request, HttpServletRequest httpRequest) {
        logger.info("Authentication request for user: {}", request.username());
        
        String ipAddress = getClientIpAddress(httpRequest);
        String userAgent = httpRequest.getHeader("User-Agent");
        
        ApiResponse<LoginResponse> response = authService.login(request);
        
        if (response.success()) {
            // Başarılı login'i logla
            loginLogService.logSuccessfulLogin(request.username(), ipAddress, userAgent);
            return ResponseEntity.ok(response);
        } else {
            // Başarısız login'i logla
            loginLogService.logFailedLogin(request.username(), ipAddress, userAgent, response.message());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
    
    @PostMapping(value = "/register", produces = "application/json", consumes = "application/json")
    // @Operation(summary = "User registration", description = "Register new user through auth service")
    // @ApiResponses(value = {
    //     @ApiResponse(responseCode = "201", description = "Registration successful"),
    //     @ApiResponse(responseCode = "400", description = "Invalid request"),
    //     @ApiResponse(responseCode = "409", description = "User already exists")
    // })
    public ResponseEntity<ApiResponse<UserDTO>> register(@Valid @RequestBody RegisterRequest request) {
        logger.info("Registration request for user: {}", request.username());
        
        ApiResponse<UserDTO> response = authService.register(request);
        
        if (response.success()) {
            return ResponseEntity.status(201).body(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @PostMapping(value = "/refresh", produces = "application/json", consumes = "application/json")
    // @Operation(summary = "Refresh access token", description = "Refresh access token through auth service")
    // @ApiResponses(value = {
    //     @ApiResponse(responseCode = "200", description = "Token refresh successful"),
    //     @ApiResponse(responseCode = "401", description = "Invalid refresh token"),
    //     @ApiResponse(responseCode = "400", description = "Invalid request")
    // })
    public ResponseEntity<ApiResponse<TokenRefreshResponse>> refreshToken(@Valid @RequestBody TokenRefreshRequest request) {
        logger.info("Token refresh request");
        
        ApiResponse<TokenRefreshResponse> response = authService.refreshToken(request);
        
        if (response.success()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @GetMapping(value = "/user", produces = "application/json")
    // @Operation(summary = "Get current user", description = "Get current user information through auth service")
    // @ApiResponses(value = {
    //     @ApiResponse(responseCode = "200", description = "User information retrieved successfully"),
    //     @ApiResponse(responseCode = "401", description = "Authentication required")
    // })
    public ResponseEntity<ApiResponse<UserDTO>> getCurrentUser(@RequestHeader("Authorization") String token) {
        logger.debug("Get current user request");
        
        // Remove "Bearer " prefix if present
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        
        ApiResponse<UserDTO> response = authService.getCurrentUser(token);
        
        if (response.success()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @PostMapping(value = "/logout", produces = "application/json")
    // @Operation(summary = "User logout", description = "Logout user through auth service")
    // @ApiResponses(value = {
    //     @ApiResponse(responseCode = "200", description = "Logout successful"),
    //     @ApiResponse(responseCode = "401", description = "Authentication required")
    // })
    public ResponseEntity<ApiResponse<Void>> logout(@RequestHeader("Authorization") String token) {
        logger.info("Logout request");
        
        // Remove "Bearer " prefix if present
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        
        ApiResponse<Void> response = authService.logout(token);
        
        if (response.success()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }
}
