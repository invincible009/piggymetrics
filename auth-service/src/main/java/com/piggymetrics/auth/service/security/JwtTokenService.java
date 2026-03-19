package com.piggymetrics.auth.service.security;

import com.piggymetrics.auth.config.JwtTokenProperties;
import io.jsonwebtoken.Jwts;
import javax.crypto.SecretKey;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;
import java.util.Set;

@Service
public class JwtTokenService {

	private final SecretKey jwtSigningKey;
	private final JwtTokenProperties properties;

	public JwtTokenService(SecretKey jwtSigningKey, JwtTokenProperties properties) {
		this.jwtSigningKey = jwtSigningKey;
		this.properties = properties;
	}

	public TokenResponse issueToken(String subject, String clientId, Set<String> scopes) {
		Instant now = Instant.now();
		Instant expiresAt = now.plusSeconds(properties.expirationSeconds());
		String token = Jwts.builder()
				.subject(subject)
				.claim("client_id", clientId)
				.claim("scope", scopes)
				.issuedAt(Date.from(now))
				.expiration(Date.from(expiresAt))
				.signWith(jwtSigningKey, Jwts.SIG.HS256)
				.compact();

		return new TokenResponse(token, "bearer", properties.expirationSeconds(), String.join(" ", scopes));
	}

	public record TokenResponse(String access_token, String token_type, long expires_in, String scope) {
	}
}
