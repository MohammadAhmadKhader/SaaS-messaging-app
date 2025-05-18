package com.example.multitenant;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.example.multitenant.testsupport.utils.BaseTests;

@SpringBootTest
class MultiTenantApplicationTests extends BaseTests {

	@Test
	void contextLoads() {
		assertTrue(true);
	}
}