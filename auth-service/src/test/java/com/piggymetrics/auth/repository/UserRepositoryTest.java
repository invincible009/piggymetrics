package com.piggymetrics.auth.repository;

import com.piggymetrics.auth.domain.User;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class UserRepositoryTest {

	private final Map<String, User> store = new HashMap<>();

	@Test
	public void shouldSaveAndFindUserByName() {

		User user = new User();
		user.setUsername("name");
		user.setPassword("password");
		store.put(user.getUsername(), user);

		Optional<User> found = Optional.ofNullable(store.get(user.getUsername()));
		assertTrue(found.isPresent());
		assertEquals(user.getUsername(), found.get().getUsername());
		assertEquals(user.getPassword(), found.get().getPassword());
	}
}
