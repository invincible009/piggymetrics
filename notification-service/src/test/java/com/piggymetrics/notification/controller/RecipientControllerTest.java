package com.piggymetrics.notification.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import com.piggymetrics.notification.domain.Frequency;
import com.piggymetrics.notification.domain.NotificationSettings;
import com.piggymetrics.notification.domain.NotificationType;
import com.piggymetrics.notification.domain.Recipient;
import com.piggymetrics.notification.service.RecipientService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
public class RecipientControllerTest {

	private static final ObjectMapper mapper = new ObjectMapper();

	private RecipientController recipientController;

	@Mock
	private RecipientService recipientService;

	private MockMvc mockMvc;

	@Before
	public void setup() {
		initMocks(this);
		this.recipientController = new RecipientController(recipientService);
		this.mockMvc = MockMvcBuilders.standaloneSetup(recipientController).build();
	}

	@Test
	public void shouldSaveCurrentRecipientSettings() throws Exception {

		Recipient recipient = getStubRecipient();
		String json = mapper.writeValueAsString(recipient);

		mockMvc.perform(put("/recipients/current").principal(() -> recipient.getAccountName()).contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isOk());
	}

	@Test
	public void shouldGetCurrentRecipientSettings() throws Exception {

		Recipient recipient = getStubRecipient();
		when(recipientService.findByAccountName(recipient.getAccountName())).thenReturn(recipient);

		Recipient result = (Recipient) recipientController.getCurrentNotificationsSettings(() -> recipient.getAccountName());
		assertEquals(recipient, result);
	}

	private Recipient getStubRecipient() {

		NotificationSettings remind = new NotificationSettings();
		remind.setActive(true);
		remind.setFrequency(Frequency.WEEKLY);
		remind.setLastNotified(null);

		NotificationSettings backup = new NotificationSettings();
		backup.setActive(false);
		backup.setFrequency(Frequency.MONTHLY);
		backup.setLastNotified(null);

		Recipient recipient = new Recipient();
		recipient.setAccountName("test");
		recipient.setEmail("test@test.com");
		recipient.setScheduledNotifications(ImmutableMap.of(
				NotificationType.BACKUP, backup,
				NotificationType.REMIND, remind
		));

		return recipient;
	}
}
