package com.piggymetrics.auth.controller;

import com.piggymetrics.auth.domain.User;
import com.piggymetrics.auth.service.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {

	private final UserService userService;

	public UserController(UserService userService) {
		this.userService = userService;
	}

	@RequestMapping(value = "/current", method = RequestMethod.GET)
	public Object getUser(Principal principal) {
		if (principal instanceof org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken jwtAuth) {
			Jwt jwt = jwtAuth.getToken();
			return Map.of(
					"name", jwt.getSubject(),
					"oauth2Request", Map.of(
							"clientId", jwt.getClaimAsString("client_id"),
							"scope", jwt.getClaimAsStringList("scope")));
		}
		return principal;
	}

	@PreAuthorize("hasAuthority('SCOPE_server')")
	@RequestMapping(method = RequestMethod.POST)
	public void createUser(@Valid @RequestBody User user) {
		userService.create(user);
	}
}
