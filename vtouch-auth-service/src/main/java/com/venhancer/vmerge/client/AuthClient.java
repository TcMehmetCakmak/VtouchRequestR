package com.venhancer.vmerge.client;

import com.venhancer.vmerge.dto.request.LoginRequest;
import com.venhancer.vmerge.dto.request.RegisterRequest;
import com.venhancer.vmerge.dto.request.TokenRefreshRequest;
import com.venhancer.vmerge.dto.response.ApiResponse;
import com.venhancer.vmerge.dto.response.LoginResponse;
import com.venhancer.vmerge.dto.response.TokenRefreshResponse;
import com.venhancer.vmerge.dto.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(
    name = "auth-service",
    url = "${auth.service.url:http://localhost:8080}",
    path = "/api/auth"
)
public interface AuthClient {
    
    @PostMapping("/login")
    ApiResponse<LoginResponse> login(@RequestBody LoginRequest request);
    
    @PostMapping("/register")
    ApiResponse<UserDTO> register(@RequestBody RegisterRequest request);
    
    @PostMapping("/refresh")
    ApiResponse<TokenRefreshResponse> refreshToken(@RequestBody TokenRefreshRequest request);
    
    @GetMapping("/me")
    ApiResponse<UserDTO> getCurrentUser(@RequestHeader("Authorization") String token);
    
    @PostMapping("/logout")
    ApiResponse<Void> logout(@RequestHeader("Authorization") String token);
    
    @PostMapping("/introspect")
    ApiResponse<UserDTO> introspectToken(@RequestHeader("Authorization") String token);
}
