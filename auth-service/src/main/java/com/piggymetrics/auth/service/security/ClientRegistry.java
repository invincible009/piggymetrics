package com.piggymetrics.auth.service.security;

import com.piggymetrics.auth.config.ClientProperties;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;

@Component
public class ClientRegistry {

	private final Map<String, RegisteredClient> clients;

	public ClientRegistry(ClientProperties properties) {
		clients = Map.of(
				properties.browserId(), new RegisteredClient(properties.browserId(), null, Set.of("password"), Set.of("ui")),
				properties.accountServiceId(), new RegisteredClient(properties.accountServiceId(), properties.accountServiceSecret(), Set.of("client_credentials"), Set.of("server")),
				properties.statisticsServiceId(), new RegisteredClient(properties.statisticsServiceId(), properties.statisticsServiceSecret(), Set.of("client_credentials"), Set.of("server")),
				properties.notificationServiceId(), new RegisteredClient(properties.notificationServiceId(), properties.notificationServiceSecret(), Set.of("client_credentials"), Set.of("server"))
		);
	}

	public RegisteredClient requireClient(String clientId) {
		RegisteredClient client = clients.get(clientId);
		if (client == null) {
			throw new IllegalArgumentException("Unknown client: " + clientId);
		}
		return client;
	}

	public record RegisteredClient(String clientId, String secret, Set<String> grantTypes, Set<String> scopes) {
	}
}
