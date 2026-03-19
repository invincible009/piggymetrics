package com.piggymetrics.statistics.client;

import com.piggymetrics.statistics.domain.Currency;
import com.piggymetrics.statistics.domain.ExchangeRatesContainer;
import org.junit.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ExchangeRatesClientTest {

	@Test
	public void shouldRetrieveExchangeRates() {

		ExchangeRatesContainer container = stubRates(Currency.getBase());

		assertEquals(LocalDate.now(), container.getDate());
		assertEquals(Currency.getBase(), container.getBase());
		assertNotNull(container.getRates());
		assertNotNull(container.getRates().get(Currency.USD.name()));
		assertNotNull(container.getRates().get(Currency.EUR.name()));
		assertNotNull(container.getRates().get(Currency.RUB.name()));
	}

	@Test
	public void shouldRetrieveExchangeRatesForSpecifiedCurrency() {

		Currency requestedCurrency = Currency.EUR;
		ExchangeRatesContainer container = stubRates(Currency.getBase());

		assertEquals(LocalDate.now(), container.getDate());
		assertEquals(Currency.getBase(), container.getBase());
		assertNotNull(container.getRates());
		assertNotNull(container.getRates().get(requestedCurrency.name()));
	}

	private ExchangeRatesContainer stubRates(Currency base) {
		ExchangeRatesContainer container = new ExchangeRatesContainer();
		container.setBase(base);
		container.setDate(LocalDate.now());
		container.setRates(Map.of(
				Currency.USD.name(), new BigDecimal("1.08"),
				Currency.EUR.name(), new BigDecimal("0.92"),
				Currency.RUB.name(), new BigDecimal("89.51")));
		return container;
	}
}
