package com.piggymetrics.auth.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "security.jwt")
public record JwtTokenProperties(String tokenSecret, long expirationSeconds) {
}
