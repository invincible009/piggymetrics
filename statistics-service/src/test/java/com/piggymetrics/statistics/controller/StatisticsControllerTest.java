package com.piggymetrics.statistics.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.piggymetrics.statistics.domain.Account;
import com.piggymetrics.statistics.domain.Currency;
import com.piggymetrics.statistics.domain.Item;
import com.piggymetrics.statistics.domain.Saving;
import com.piggymetrics.statistics.domain.TimePeriod;
import com.piggymetrics.statistics.domain.timeseries.DataPoint;
import com.piggymetrics.statistics.domain.timeseries.DataPointId;
import com.piggymetrics.statistics.service.StatisticsService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.Date;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.mockito.internal.verification.VerificationModeFactory.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
public class StatisticsControllerTest {

	private static final ObjectMapper mapper = new ObjectMapper();

	private StatisticsController statisticsController;

	@Mock
	private StatisticsService statisticsService;

	private MockMvc mockMvc;

	@Before
	public void setup() {
		initMocks(this);
		this.statisticsController = new StatisticsController(statisticsService);
		this.mockMvc = MockMvcBuilders.standaloneSetup(statisticsController).build();
	}

	@Test
	public void shouldGetStatisticsByAccountName() throws Exception {

		final DataPoint dataPoint = new DataPoint();
		dataPoint.setId(new DataPointId("test", new Date()));

		when(statisticsService.findByAccountName(dataPoint.getId().getAccount()))
				.thenReturn(ImmutableList.of(dataPoint));

		org.junit.Assert.assertEquals(
				ImmutableList.of(dataPoint),
				statisticsController.getStatisticsByAccountName(dataPoint.getId().getAccount()));
	}

	@Test
	public void shouldGetCurrentAccountStatistics() throws Exception {

		final DataPoint dataPoint = new DataPoint();
		dataPoint.setId(new DataPointId("test", new Date()));

		when(statisticsService.findByAccountName(dataPoint.getId().getAccount()))
				.thenReturn(ImmutableList.of(dataPoint));

		org.junit.Assert.assertEquals(
				ImmutableList.of(dataPoint),
				statisticsController.getCurrentAccountStatistics(() -> dataPoint.getId().getAccount()));
	}

	@Test
	public void shouldSaveAccountStatistics() throws Exception {

		Saving saving = new Saving();
		saving.setAmount(new BigDecimal(1500));
		saving.setCurrency(Currency.USD);
		saving.setInterest(new BigDecimal("3.32"));
		saving.setDeposit(true);
		saving.setCapitalization(false);

		Item grocery = new Item();
		grocery.setTitle("Grocery");
		grocery.setAmount(new BigDecimal(10));
		grocery.setCurrency(Currency.USD);
		grocery.setPeriod(TimePeriod.DAY);

		Item salary = new Item();
		salary.setTitle("Salary");
		salary.setAmount(new BigDecimal(9100));
		salary.setCurrency(Currency.USD);
		salary.setPeriod(TimePeriod.MONTH);

		final Account account = new Account();
		account.setSaving(saving);
		account.setExpenses(ImmutableList.of(grocery));
		account.setIncomes(ImmutableList.of(salary));

		String json = mapper.writeValueAsString(account);

		mockMvc.perform(put("/test").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isOk());

		verify(statisticsService, times(1)).save(anyString(), any(Account.class));
	}
}
