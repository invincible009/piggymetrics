package com.piggymetrics.account.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "security.jwt")
public record JwtTokenProperties(String tokenSecret) {
}
