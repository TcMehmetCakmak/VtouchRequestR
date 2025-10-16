package com.venhancer.vmerge.security;

import com.venhancer.vmerge.dto.response.ApiResponse;
import com.venhancer.vmerge.dto.response.ErrorDetail;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationEntryPoint.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {
        
        logger.warn("Unauthorized access attempt: {} - {}", request.getRequestURI(), authException.getMessage());
        
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        
        List<ErrorDetail> errors = List.of(
            ErrorDetail.global("UNAUTHORIZED", "Authentication is required to access this resource")
        );
        
        ApiResponse<Object> apiResponse = ApiResponse.error(
            "Authentication is required to access this resource",
            errors
        ).withPath(request.getRequestURI());
        
        objectMapper.writeValue(response.getOutputStream(), apiResponse);
    }
} 