package com.piggymetrics.notification.config;

import feign.RequestInterceptor;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.Nullable;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestClient;

import javax.crypto.SecretKey;
import java.util.List;

@Configuration
@EnableConfigurationProperties({OAuthClientProperties.class, JwtTokenProperties.class})
public class SecurityConfig {

	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthenticationConverter jwtAuthenticationConverter) throws Exception {
		http
				.csrf(csrf -> csrf.disable())
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.authorizeHttpRequests(auth -> auth
						.requestMatchers("/actuator/**").permitAll()
						.anyRequest().authenticated())
				.oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter)));
		return http.build();
	}

	@Bean
	SecretKey jwtSigningKey(JwtTokenProperties properties) {
		return Keys.hmacShaKeyFor(Decoders.BASE64.decode(properties.tokenSecret()));
	}

	@Bean
	JwtDecoder jwtDecoder(SecretKey jwtSigningKey) {
		return NimbusJwtDecoder.withSecretKey(jwtSigningKey).build();
	}

	@Bean
	JwtAuthenticationConverter jwtAuthenticationConverter() {
		JwtGrantedAuthoritiesConverter scopes = new JwtGrantedAuthoritiesConverter();
		scopes.setAuthorityPrefix("SCOPE_");
		scopes.setAuthoritiesClaimName("scope");

		JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
		converter.setJwtGrantedAuthoritiesConverter(scopes);
		return converter;
	}

	@Bean
	ServiceTokenClient serviceTokenClient(RestClient.Builder builder, OAuthClientProperties properties) {
		return new ServiceTokenClient(builder.build(), properties);
	}

	@Bean
	RequestInterceptor oauth2RequestInterceptor(ServiceTokenClient serviceTokenClient) {
		return requestTemplate -> requestTemplate.header(HttpHeaders.AUTHORIZATION, "Bearer " + resolveBearerToken(serviceTokenClient));
	}

	private String resolveBearerToken(ServiceTokenClient serviceTokenClient) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication != null && authentication.getPrincipal() instanceof Jwt jwt) {
			return jwt.getTokenValue();
		}
		return serviceTokenClient.getAccessToken();
	}

	static class ServiceTokenClient {
		private final RestClient restClient;
		private final OAuthClientProperties properties;

		ServiceTokenClient(RestClient restClient, OAuthClientProperties properties) {
			this.restClient = restClient;
			this.properties = properties;
		}

		String getAccessToken() {
			LinkedMultiValueMap<String, String> form = new LinkedMultiValueMap<>();
			form.put("grant_type", List.of("client_credentials"));
			TokenResponse token = restClient.post()
					.uri(properties.accessTokenUri())
					.contentType(MediaType.APPLICATION_FORM_URLENCODED)
					.headers(headers -> headers.setBasicAuth(properties.clientId(), properties.clientSecret()))
					.body(form)
					.retrieve()
					.body(TokenResponse.class);
			if (token == null || token.access_token == null) {
				throw new IllegalStateException("Auth service returned no access token");
			}
			return token.access_token;
		}
	}

	static class TokenResponse {
		@Nullable
		public String access_token;
	}
}
