package com.piggymetrics.auth;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import com.piggymetrics.auth.repository.UserRepository;
import com.piggymetrics.auth.service.security.MongoUserDetailsService;

@RunWith(SpringRunner.class)
@SpringBootTest(properties = {
		"spring.cloud.config.enabled=false",
		"spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration,org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration,org.springframework.boot.autoconfigure.data.mongo.MongoRepositoriesAutoConfiguration",
		"security.jwt.token-secret=cGlnZ3ltZXRyaWNzLWphdmEtMjEtc2hhcmVkLXNlY3JldC1rZXktMDEyMzQ1Njc4OTA=",
		"security.jwt.expiration-seconds=3600",
		"security.clients.browser-id=browser",
		"security.clients.account-service-id=account-service",
		"security.clients.account-service-secret=test-account-secret",
		"security.clients.statistics-service-id=statistics-service",
		"security.clients.statistics-service-secret=test-statistics-secret",
		"security.clients.notification-service-id=notification-service",
		"security.clients.notification-service-secret=test-notification-secret"
})
public class AuthServiceApplicationTests {

	@MockBean
	private MongoUserDetailsService userDetailsService;

	@MockBean
	private UserRepository userRepository;

	@Test
	public void contextLoads() {
	}

}
