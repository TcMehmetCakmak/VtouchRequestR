package com.venhancer.vmerge.config;

import com.venhancer.vmerge.security.JwtAuthenticationEntryPoint;
import com.venhancer.vmerge.security.JwtAuthenticationFilter;
import com.venhancer.vmerge.service.UserService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@ConditionalOnProperty(
        prefix = "spring.security",
        name = "enabled",
        havingValue = "true",
        matchIfMissing = true)
@EnableConfigurationProperties(SecurityProperties.class)
public class SecurityConfig {

    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final SecurityProperties securityProperties;

    public SecurityConfig(JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint,
                          JwtAuthenticationFilter jwtAuthenticationFilter, SecurityProperties securityProperties) {
        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.securityProperties = securityProperties;
    }

    @Bean
    public AuthenticationProvider authenticationProvider(UserService userService, PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userService);
        authProvider.setPasswordEncoder(passwordEncoder);
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public SecurityFilterChain filterChain(@NonNull HttpSecurity http, UserService userService, PasswordEncoder passwordEncoder) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(
                        session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(exception -> exception.authenticationEntryPoint(jwtAuthenticationEntryPoint))
                .authorizeHttpRequests(
                        auth ->
                                auth
                                        // Public endpoints (no authentication required)
                                        .requestMatchers(securityProperties.publicEndpoints().toArray(String[]::new))
                                        .permitAll()
                                        //.requestMatchers("/auth/**").permitAll()
                                        //.requestMatchers("/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**").permitAll()
                                        //.requestMatchers("/actuator/health").permitAll()
                                        .requestMatchers("/simple/hello").permitAll()
                                        
                                        // Admin-only endpoints
                                        .requestMatchers("/users", "POST").hasAnyRole("ADMIN","USER")
                                        .requestMatchers("/users/*/role", "PATCH").hasRole("ADMIN")
                                        .requestMatchers("/users/*", "DELETE").hasRole("ADMIN")
                                        .requestMatchers(HttpMethod.GET,"/users/*").hasRole("ADMIN")

                                        // Admin and Moderator endpoints
                                        .requestMatchers("/users", "GET").hasAnyRole("ADMIN", "MODERATOR")
                                        .requestMatchers("/users/search", "GET").hasAnyRole("ADMIN", "MODERATOR")
                                        .requestMatchers("/users/statistics", "GET").hasAnyRole("ADMIN", "MODERATOR")
                                        .requestMatchers("/users/recent", "GET").hasAnyRole("ADMIN", "MODERATOR")
                                        .requestMatchers("/users/*/status", "PATCH").hasAnyRole("ADMIN", "MODERATOR")
                                        .requestMatchers("/users/by-status/*", "GET").hasAnyRole("ADMIN", "MODERATOR")
                                        .requestMatchers("/users/by-role/*", "GET").hasAnyRole("ADMIN", "MODERATOR")
                                        
                                        // User-specific endpoints (users can access their own data)
                                        .requestMatchers("/users/*", "GET").authenticated()
                                        .requestMatchers("/users/*", "PUT").authenticated()
                                        .requestMatchers("/users/username/*", "GET").authenticated()
                                        
                                        // Security examples (for testing)
                                        .requestMatchers("/api/security-examples/admin-only", "GET").hasRole("ADMIN")
                                        .requestMatchers("/api/security-examples/admin-or-moderator", "GET").hasAnyRole("ADMIN", "MODERATOR")
                                        .requestMatchers("/api/security-examples/authenticated", "GET").authenticated()
                                        .requestMatchers("/api/security-examples/complex-auth", "GET").authenticated()
                                        .requestMatchers("/api/security-examples/post-auth/*", "GET").authenticated()
                                        .requestMatchers("/api/security-examples/custom-permission", "GET").authenticated()
                                        .requestMatchers("/api/security-examples/user/*", "PUT").authenticated()
                                        .requestMatchers("/api/security-examples/no-users", "GET").hasAnyRole("ADMIN", "MODERATOR")
                                        .requestMatchers("/api/security-examples/current-user", "GET").authenticated()
                                        .requestMatchers("/api/security-examples/conditional-access", "GET").authenticated()
                                        
                                        // Security test endpoints (for testing centralized security)
                                        .requestMatchers("/api/test/admin-only", "GET").hasRole("ADMIN")
                                        .requestMatchers("/api/test/admin-or-moderator", "GET").hasAnyRole("ADMIN", "MODERATOR")
                                        .requestMatchers("/api/test/authenticated", "GET").authenticated()
                                        .requestMatchers("/api/test/current-user", "GET").authenticated()
                                        .requestMatchers("/api/test/method-test", "POST").hasRole("ADMIN")
                                        .requestMatchers("/api/test/method-test", "PUT").hasAnyRole("ADMIN", "MODERATOR")
                                        .requestMatchers("/api/test/method-test", "DELETE").hasRole("ADMIN")
                                        
                                        // All other endpoints require authentication
                                        .anyRequest()
                                        .authenticated()
                )
                .authenticationProvider(authenticationProvider(userService, passwordEncoder))
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
} 