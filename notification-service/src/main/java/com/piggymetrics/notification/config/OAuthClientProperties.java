package com.piggymetrics.notification.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "security.oauth2.client")
public record OAuthClientProperties(String clientId, String clientSecret, String accessTokenUri) {
}
