package com.piggymetrics.statistics;

import com.piggymetrics.statistics.repository.DataPointRepository;
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
public class StatisticsServiceApplicationTests {

	@MockBean
	private DataPointRepository dataPointRepository;

	@Test
	public void contextLoads() {
	}

}
