package com.venhancer.vmerge.security;

import com.venhancer.vmerge.service.JwtService;
import com.venhancer.vmerge.service.UserService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SecurityException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtService jwtService;
    private final UserService userService;

    public JwtAuthenticationFilter(JwtService jwtService, UserService userService) {
        this.jwtService = jwtService;
        this.userService = userService;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        
        logger.info("JWT Filter doFilterInternal called for path: {}", request.getRequestURI());
        
        String authHeader = request.getHeader(AUTHORIZATION_HEADER);
        
        // Skip processing if no Authorization header or doesn't start with Bearer
        if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
            filterChain.doFilter(request, response);
            return;
        }
        
        String jwtToken = authHeader.substring(BEARER_PREFIX.length());
        String username = null;
        
        try {
            username = jwtService.extractUsername(jwtToken);
        } catch (ExpiredJwtException e) {
            logger.warn("JWT token has expired: {}", e.getMessage());
            request.setAttribute("jwt.expired", true);
        } catch (MalformedJwtException e) {
            logger.warn("JWT token is malformed: {}", e.getMessage());
            request.setAttribute("jwt.malformed", true);
        } catch (SecurityException e) {
            logger.warn("JWT signature validation failed: {}", e.getMessage());
            request.setAttribute("jwt.invalid", true);
        } catch (Exception e) {
            logger.warn("JWT token processing failed: {}", e.getMessage());
            request.setAttribute("jwt.error", true);
        }
        
        // If we have a valid username and no existing authentication
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                UserDetails userDetails = userService.loadUserByUsername(username);
                
                // Validate the token
                if (jwtService.isTokenValid(jwtToken, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken = 
                        new UsernamePasswordAuthenticationToken(
                            userDetails, 
                            null, 
                            userDetails.getAuthorities()
                        );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    
                    logger.debug("User {} authenticated successfully", username);
                } else {
                    logger.warn("JWT token validation failed for user: {}", username);
                    request.setAttribute("jwt.invalid", true);
                }
            } catch (Exception e) {
                logger.warn("User authentication failed: {}", e.getMessage());
                request.setAttribute("jwt.auth.failed", true);
            }
        }
        
        filterChain.doFilter(request, response);
    }
    
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        
        logger.info("JWT Filter shouldNotFilter check for path: {}", path);
        
        // Skip JWT processing for public endpoints
        boolean shouldSkip = path.startsWith("/api/auth/") ||
               path.startsWith("/api/health") ||
               path.startsWith("/api/info") ||
               path.startsWith("/api/simple") ||
               path.startsWith("/api/v3/api-docs") ||
               path.startsWith("/api/swagger-ui") ||
               path.startsWith("/api/h2-console") ||
               path.equals("/api/swagger-ui.html");
               
        logger.info("JWT Filter shouldNotFilter result: {} for path: {}", shouldSkip, path);
        return shouldSkip;
    }
} 