package com.piggymetrics.account.client;

import com.piggymetrics.account.domain.Account;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(OutputCaptureExtension.class)
class StatisticsServiceClientFallbackTest {

	private final StatisticsServiceClientFallback statisticsServiceClient = new StatisticsServiceClientFallback();

	@Test
	void testUpdateStatisticsWithFailFallback(CapturedOutput output) {
		statisticsServiceClient.updateStatistics("test", new Account());
		assertThat(output.getOut()).contains("Error during update statistics for account: test");
	}
}
