package com.neshtek.expertconnect.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JwtServiceConfig {
    @Bean
    JwtService jwtService(
            @Value("${app.security.jwt.secret:change-this-development-secret-key-please}") String secret,
            @Value("${app.security.jwt.expiration-seconds:3600}") long expirationSeconds) {
        return new JwtService(secret, expirationSeconds);
    }
}
