package com.piggymetrics.notification;

import com.piggymetrics.notification.repository.RecipientRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(properties = {
		"spring.cloud.config.enabled=false",
		"spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration,org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration,org.springframework.boot.autoconfigure.data.mongo.MongoRepositoriesAutoConfiguration",
		"security.jwt.token-secret=cGlnZ3ltZXRyaWNzLWphdmEtMjEtc2hhcmVkLXNlY3JldC1rZXktMDEyMzQ1Njc4OTA=",
		"security.oauth2.client.client-id=notification-service",
		"security.oauth2.client.client-secret=test-notification-secret",
		"security.oauth2.client.access-token-uri=http://localhost/uaa/oauth/token",
		"spring.main.allow-bean-definition-overriding=true"
})
public class NotificationServiceApplicationTests {

	@MockBean
	private RecipientRepository recipientRepository;

	@Test
	public void contextLoads() {
	}

}
