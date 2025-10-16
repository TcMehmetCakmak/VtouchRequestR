package com.venhancer.vmerge.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class SessionTokenExtractor implements BearerTokenResolver {

    @Override
    public String resolve(HttpServletRequest request) {
        // First try to get token from Authorization header (standard Bearer token)
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        
        // Try to get from X-Auth-Token header (alternative header)
        String authToken = request.getHeader("X-Auth-Token");
        if (StringUtils.hasText(authToken)) {
            return authToken;
        }
        
        // Try to get from session (for web applications)
        String sessionToken = (String) request.getSession().getAttribute("access_token");
        if (StringUtils.hasText(sessionToken)) {
            return sessionToken;
        }
        
        // Try to get from request parameter (for specific use cases like OAuth callbacks)
        String paramToken = request.getParameter("access_token");
        if (StringUtils.hasText(paramToken)) {
            return paramToken;
        }
        
        // Try to get from cookie (for web applications)
        if (request.getCookies() != null) {
            for (jakarta.servlet.http.Cookie cookie : request.getCookies()) {
                if ("access_token".equals(cookie.getName()) && StringUtils.hasText(cookie.getValue())) {
                    return cookie.getValue();
                }
            }
        }
        
        return null;
    }
}
