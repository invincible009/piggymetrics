package com.piggymetrics.notification.repository;

import com.google.common.collect.ImmutableMap;
import com.piggymetrics.notification.domain.Frequency;
import com.piggymetrics.notification.domain.NotificationSettings;
import com.piggymetrics.notification.domain.NotificationType;
import com.piggymetrics.notification.domain.Recipient;
import org.apache.commons.lang.time.DateUtils;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class RecipientRepositoryTest {

	private final List<Recipient> repository = new ArrayList<>();

	@Test
	public void shouldFindByAccountName() {

		NotificationSettings remind = new NotificationSettings();
		remind.setActive(true);
		remind.setFrequency(Frequency.WEEKLY);
		remind.setLastNotified(new Date(0));

		NotificationSettings backup = new NotificationSettings();
		backup.setActive(false);
		backup.setFrequency(Frequency.MONTHLY);
		backup.setLastNotified(new Date());

		Recipient recipient = new Recipient();
		recipient.setAccountName("test");
		recipient.setEmail("test@test.com");
		recipient.setScheduledNotifications(ImmutableMap.of(
				NotificationType.BACKUP, backup,
				NotificationType.REMIND, remind
		));

		save(recipient);

		Recipient found = findByAccountName(recipient.getAccountName());
		assertEquals(recipient.getAccountName(), found.getAccountName());
		assertEquals(recipient.getEmail(), found.getEmail());
		assertEquals(recipient.getScheduledNotifications().get(NotificationType.BACKUP).getActive(),
				found.getScheduledNotifications().get(NotificationType.BACKUP).getActive());
		assertEquals(recipient.getScheduledNotifications().get(NotificationType.REMIND).getFrequency(),
				found.getScheduledNotifications().get(NotificationType.REMIND).getFrequency());
	}

	@Test
	public void shouldFindReadyForRemindWhenFrequencyIsWeeklyAndLastNotifiedWas8DaysAgo() {

		NotificationSettings remind = new NotificationSettings();
		remind.setActive(true);
		remind.setFrequency(Frequency.WEEKLY);
		remind.setLastNotified(DateUtils.addDays(new Date(), -8));

		Recipient recipient = new Recipient();
		recipient.setAccountName("test");
		recipient.setEmail("test@test.com");
		recipient.setScheduledNotifications(ImmutableMap.of(NotificationType.REMIND, remind));

		save(recipient);

		List<Recipient> found = findReadyFor(NotificationType.REMIND);
		assertFalse(found.isEmpty());
	}

	@Test
	public void shouldNotFindReadyForRemindWhenFrequencyIsWeeklyAndLastNotifiedWasYesterday() {

		NotificationSettings remind = new NotificationSettings();
		remind.setActive(true);
		remind.setFrequency(Frequency.WEEKLY);
		remind.setLastNotified(DateUtils.addDays(new Date(), -1));

		Recipient recipient = new Recipient();
		recipient.setAccountName("test");
		recipient.setEmail("test@test.com");
		recipient.setScheduledNotifications(ImmutableMap.of(NotificationType.REMIND, remind));

		save(recipient);

		List<Recipient> found = findReadyFor(NotificationType.REMIND);
		assertTrue(found.isEmpty());
	}

	@Test
	public void shouldNotFindReadyForRemindWhenNotificationIsNotActive() {

		NotificationSettings remind = new NotificationSettings();
		remind.setActive(false);
		remind.setFrequency(Frequency.WEEKLY);
		remind.setLastNotified(DateUtils.addDays(new Date(), -30));

		Recipient recipient = new Recipient();
		recipient.setAccountName("test");
		recipient.setEmail("test@test.com");
		recipient.setScheduledNotifications(ImmutableMap.of(NotificationType.REMIND, remind));

		save(recipient);

		List<Recipient> found = findReadyFor(NotificationType.REMIND);
		assertTrue(found.isEmpty());
	}

	@Test
	public void shouldNotFindReadyForBackupWhenFrequencyIsQuaterly() {

		NotificationSettings backup = new NotificationSettings();
		backup.setActive(true);
		backup.setFrequency(Frequency.QUARTERLY);
		backup.setLastNotified(DateUtils.addDays(new Date(), -91));

		Recipient recipient = new Recipient();
		recipient.setAccountName("test");
		recipient.setEmail("test@test.com");
		recipient.setScheduledNotifications(ImmutableMap.of(NotificationType.BACKUP, backup));

		save(recipient);

		List<Recipient> found = findReadyFor(NotificationType.BACKUP);
		assertFalse(found.isEmpty());
	}

	private void save(Recipient recipient) {
		repository.removeIf(existing -> existing.getAccountName().equals(recipient.getAccountName()));
		repository.add(recipient);
	}

	private Recipient findByAccountName(String accountName) {
		return repository.stream()
				.filter(recipient -> recipient.getAccountName().equals(accountName))
				.findFirst()
				.orElse(null);
	}

	private List<Recipient> findReadyFor(NotificationType type) {
		Date now = new Date();
		return repository.stream()
				.filter(recipient -> recipient.getScheduledNotifications().containsKey(type))
				.filter(recipient -> {
					NotificationSettings settings = recipient.getScheduledNotifications().get(type);
					if (!settings.getActive()) {
						return false;
					}
					int requiredDays = settings.getFrequency().getDays();
					Date lastNotified = Objects.requireNonNullElse(settings.getLastNotified(), new Date(0));
					return DateUtils.addDays(lastNotified, requiredDays).before(now);
				})
				.toList();
	}
}
