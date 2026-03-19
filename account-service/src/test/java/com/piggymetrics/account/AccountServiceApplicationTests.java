package com.piggymetrics.account;

import com.piggymetrics.account.repository.AccountRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(properties = {
		"spring.cloud.config.enabled=false",
		"spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration,org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration,org.springframework.boot.autoconfigure.data.mongo.MongoRepositoriesAutoConfiguration",
		"security.jwt.token-secret=cGlnZ3ltZXRyaWNzLWphdmEtMjEtc2hhcmVkLXNlY3JldC1rZXktMDEyMzQ1Njc4OTA="
})
public class AccountServiceApplicationTests {

	@MockBean
	private AccountRepository accountRepository;

	@Test
	public void contextLoads() {

	}

}
