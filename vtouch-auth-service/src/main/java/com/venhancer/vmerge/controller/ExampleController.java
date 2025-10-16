package com.venhancer.vmerge.controller;

import com.venhancer.vmerge.dto.response.ApiResponse;
import com.venhancer.vmerge.service.ExampleService;
// import io.swagger.v3.oas.annotations.Operation;
// import io.swagger.v3.oas.annotations.responses.ApiResponse;
// import io.swagger.v3.oas.annotations.responses.ApiResponses;
// import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/example")
// @Tag(name = "Example Service", description = "Example endpoints that demonstrate token-based authentication")
public class ExampleController {
    
    private static final Logger logger = LoggerFactory.getLogger(ExampleController.class);
    
    private final ExampleService exampleService;
    
    public ExampleController(ExampleService exampleService) {
        this.exampleService = exampleService;
    }
    
    @PostMapping(value = "/authenticated-operation", produces = "application/json")
    // @Operation(summary = "Perform authenticated operation", description = "Example of an operation that requires authentication")
    // @ApiResponses(value = {
    //     @ApiResponse(responseCode = "200", description = "Operation completed successfully"),
    //     @ApiResponse(responseCode = "400", description = "Authentication failed"),
    //     @ApiResponse(responseCode = "401", description = "Authentication required")
    // })
    public ResponseEntity<ApiResponse<String>> performAuthenticatedOperation(
            @RequestHeader("Authorization") String token) {
        
        logger.info("Authenticated operation request received");
        
        // Remove "Bearer " prefix if present
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        
        ApiResponse<String> response = exampleService.performAuthenticatedOperation(token);
        
        if (response.success()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @PostMapping(value = "/operation-with-refresh", produces = "application/json")
    // @Operation(summary = "Perform operation with token refresh", description = "Example of an operation that refreshes the token")
    // @ApiResponses(value = {
    //     @ApiResponse(responseCode = "200", description = "Operation completed successfully"),
    //     @ApiResponse(responseCode = "400", description = "Token refresh failed"),
    //     @ApiResponse(responseCode = "401", description = "Invalid refresh token")
    // })
    public ResponseEntity<ApiResponse<String>> performOperationWithTokenRefresh(
            @RequestHeader("X-Refresh-Token") String refreshToken) {
        
        logger.info("Operation with token refresh request received");
        
        ApiResponse<String> response = exampleService.performOperationWithTokenRefresh(refreshToken);
        
        if (response.success()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @GetMapping(value = "/health", produces = "application/json")
    // @Operation(summary = "Health check", description = "Simple health check endpoint")
    // @ApiResponses(value = {
    //     @ApiResponse(responseCode = "200", description = "Service is healthy")
    // })
    public ResponseEntity<ApiResponse<String>> healthCheck() {
        logger.debug("Health check request received");
        
        String status = "Login service is running and healthy";
        ApiResponse<String> response = ApiResponse.success("Service is healthy", status);
        
        return ResponseEntity.ok(response);
    }
}
