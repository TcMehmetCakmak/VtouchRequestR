package com.venhancer.vmerge.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "spring.security")
public record SecurityProperties(List<String> publicEndpoints, AccessTokenFilterProperties accessTokenFilter) {

    public record AccessTokenFilterProperties(List<String> skippedPaths) {}
}