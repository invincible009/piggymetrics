package com.piggymetrics.auth.controller;

import com.piggymetrics.auth.service.security.ClientRegistry;
import com.piggymetrics.auth.service.security.JwtTokenService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@RestController
public class TokenController {

	private final AuthenticationManager authenticationManager;
	private final ClientRegistry clientRegistry;
	private final JwtTokenService jwtTokenService;

	public TokenController(AuthenticationManager authenticationManager, ClientRegistry clientRegistry,
						   JwtTokenService jwtTokenService) {
		this.authenticationManager = authenticationManager;
		this.clientRegistry = clientRegistry;
		this.jwtTokenService = jwtTokenService;
	}

	@PostMapping(path = "/oauth/token", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
	public JwtTokenService.TokenResponse token(@RequestParam("grant_type") String grantType,
											   @RequestParam(value = "username", required = false) String username,
											   @RequestParam(value = "password", required = false) String password,
											   @RequestParam(value = "client_id", required = false) String clientId,
											   @RequestParam(value = "client_secret", required = false) String clientSecret,
											   @RequestHeader(value = "Authorization", required = false) String authorizationHeader) {
		ClientCredentials clientCredentials = resolveClientCredentials(clientId, clientSecret, authorizationHeader);
		ClientRegistry.RegisteredClient client = clientRegistry.requireClient(clientCredentials.clientId());

		if (!client.grantTypes().contains(grantType)) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unsupported grant type for client");
		}

		if (client.secret() != null && !client.secret().equals(clientCredentials.clientSecret())) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid client credentials");
		}

		if ("password".equals(grantType)) {
			if (!StringUtils.hasText(username) || !StringUtils.hasText(password)) {
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "username and password are required");
			}
			authenticateUser(username, password);
			return jwtTokenService.issueToken(username, client.clientId(), client.scopes());
		}

		if ("client_credentials".equals(grantType)) {
			return jwtTokenService.issueToken(client.clientId(), client.clientId(), client.scopes());
		}

		throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unsupported grant type");
	}

	private void authenticateUser(String username, String password) {
		try {
			Authentication authentication = authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(username, password));
			if (!authentication.isAuthenticated()) {
				throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication failed");
			}
		} catch (AuthenticationException exception) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication failed", exception);
		}
	}

	private ClientCredentials resolveClientCredentials(String clientId, String clientSecret, String authorizationHeader) {
		if (StringUtils.hasText(authorizationHeader) && authorizationHeader.startsWith("Basic ")) {
			String decoded = new String(Base64.getDecoder().decode(authorizationHeader.substring(6)), StandardCharsets.UTF_8);
			int separator = decoded.indexOf(':');
			if (separator < 0) {
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid basic auth header");
			}
			return new ClientCredentials(decoded.substring(0, separator), decoded.substring(separator + 1));
		}
		if (!StringUtils.hasText(clientId)) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "client_id is required");
		}
		return new ClientCredentials(clientId, clientSecret);
	}

	private record ClientCredentials(String clientId, String clientSecret) {
	}
}
