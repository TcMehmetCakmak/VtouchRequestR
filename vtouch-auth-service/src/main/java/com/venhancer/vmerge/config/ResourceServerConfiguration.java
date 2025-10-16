package com.venhancer.vmerge.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class ResourceServerConfiguration {

  private final BearerTokenResolver tokenResolver;

  public ResourceServerConfiguration(SessionTokenExtractor tokenResolver) {
    this.tokenResolver = tokenResolver;
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        .csrf(csrf -> csrf.disable())
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/actuator/**").permitAll()
            .requestMatchers("/api/login/authenticate").permitAll()
            .requestMatchers("/api/login/register").permitAll()
            .requestMatchers("/api/login/refresh").permitAll()
            .requestMatchers("/swagger-ui/**").permitAll()
            .requestMatchers("/api-docs/**").permitAll()
            .requestMatchers("/health").permitAll()
            .anyRequest().authenticated()
        )
        .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .oauth2ResourceServer(oauth2 -> oauth2
          .bearerTokenResolver(tokenResolver)
          .jwt(Customizer.withDefaults())
        )
        .headers(headers -> headers.frameOptions(frame -> frame.disable()));

    return http.build();
  }
}
