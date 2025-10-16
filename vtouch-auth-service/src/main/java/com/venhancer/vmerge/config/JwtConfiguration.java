package com.venhancer.vmerge.config;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.UUID;

@Configuration
public class JwtConfiguration {

    @Value("${auth.service.jwks-uri:}")
    private String jwksUri;

    @Value("${auth.service.url:http://localhost:8080}")
    private String authServiceUrl;

    @Bean
    public JWKSource<SecurityContext> jwkSource() throws Exception {
        // If external JWKS URI is provided, use it
        if (jwksUri != null && !jwksUri.isEmpty()) {
            // For external JWKS, you would typically use a RemoteJWKSet
            // This is a simplified version - in production you might want to use RemoteJWKSet
            return createLocalJWKSource();
        }
        
        // Otherwise, create a local JWK source (for development/testing)
        return createLocalJWKSource();
    }

    private JWKSource<SecurityContext> createLocalJWKSource() throws Exception {
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
        kpg.initialize(2048);
        KeyPair kp = kpg.generateKeyPair();
        RSAPublicKey publicKey = (RSAPublicKey) kp.getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey) kp.getPrivate();
        RSAKey rsaKey = new RSAKey.Builder(publicKey)
                .privateKey(privateKey)
                .keyID(UUID.randomUUID().toString())
                .build();
        JWKSet jwkSet = new JWKSet(rsaKey);
        return new ImmutableJWKSet<>(jwkSet);
    }

    @Bean
    public JwtDecoder jwtDecoder(JWKSource<SecurityContext> jwkSource) {
        // If using external JWKS URI, create decoder with that URI
        if (jwksUri != null && !jwksUri.isEmpty()) {
            return NimbusJwtDecoder.withJwkSetUri(jwksUri).build();
        }
        
        // Otherwise, use the local JWK source
        return NimbusJwtDecoder.withJwkSetUri(authServiceUrl + "/.well-known/jwks.json").build();
    }
}
