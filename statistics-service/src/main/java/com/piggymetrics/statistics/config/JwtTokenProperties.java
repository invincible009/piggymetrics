package com.piggymetrics.statistics.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "security.jwt")
public record JwtTokenProperties(String tokenSecret) {
}
