package com.piggymetrics.statistics.repository;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import com.piggymetrics.statistics.domain.timeseries.DataPoint;
import com.piggymetrics.statistics.domain.timeseries.DataPointId;
import com.piggymetrics.statistics.domain.timeseries.ItemMetric;
import com.piggymetrics.statistics.domain.timeseries.StatisticMetric;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class DataPointRepositoryTest {

	private final List<DataPoint> repository = new ArrayList<>();

	@Test
	public void shouldSaveDataPoint() {

		ItemMetric salary = new ItemMetric("salary", new BigDecimal(20_000));

		ItemMetric grocery = new ItemMetric("grocery", new BigDecimal(1_000));
		ItemMetric vacation = new ItemMetric("vacation", new BigDecimal(2_000));

		DataPointId pointId = new DataPointId("test-account", new Date(0));

		DataPoint point = new DataPoint();
		point.setId(pointId);
		point.setIncomes(Sets.newHashSet(salary));
		point.setExpenses(Sets.newHashSet(grocery, vacation));
		point.setStatistics(ImmutableMap.of(
				StatisticMetric.SAVING_AMOUNT, new BigDecimal(400_000),
				StatisticMetric.INCOMES_AMOUNT, new BigDecimal(20_000),
				StatisticMetric.EXPENSES_AMOUNT, new BigDecimal(3_000)
		));

		save(point);

		List<DataPoint> points = findByAccount(pointId.getAccount());
		assertEquals(1, points.size());
		assertEquals(pointId.getDate(), points.get(0).getId().getDate());
		assertEquals(point.getStatistics().size(), points.get(0).getStatistics().size());
		assertEquals(point.getIncomes().size(), points.get(0).getIncomes().size());
		assertEquals(point.getExpenses().size(), points.get(0).getExpenses().size());
	}

	@Test
	public void shouldRewriteDataPointWithinADay() {

		final BigDecimal earlyAmount = new BigDecimal(100);
		final BigDecimal lateAmount = new BigDecimal(200);

		DataPointId pointId = new DataPointId("test-account", new Date(0));

		DataPoint earlier = new DataPoint();
		earlier.setId(pointId);
		earlier.setStatistics(ImmutableMap.of(
				StatisticMetric.SAVING_AMOUNT, earlyAmount
		));

		save(earlier);

		DataPoint later = new DataPoint();
		later.setId(pointId);
		later.setStatistics(ImmutableMap.of(
				StatisticMetric.SAVING_AMOUNT, lateAmount
		));

		save(later);

		List<DataPoint> points = findByAccount(pointId.getAccount());

		assertEquals(1, points.size());
		assertEquals(lateAmount, points.get(0).getStatistics().get(StatisticMetric.SAVING_AMOUNT));
	}

	private void save(DataPoint point) {
		repository.removeIf(existing -> existing.getId().getAccount().equals(point.getId().getAccount())
				&& existing.getId().getDate().equals(point.getId().getDate()));
		repository.add(point);
	}

	private List<DataPoint> findByAccount(String accountName) {
		return repository.stream()
				.filter(point -> point.getId().getAccount().equals(accountName))
				.toList();
	}
}
