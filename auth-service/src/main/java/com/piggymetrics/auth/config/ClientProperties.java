package com.piggymetrics.auth.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "security.clients")
public record ClientProperties(String browserId, String accountServiceId, String accountServiceSecret,
							   String statisticsServiceId, String statisticsServiceSecret,
							   String notificationServiceId, String notificationServiceSecret) {
}
