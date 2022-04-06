package com.my.azusato.test;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import org.junit.jupiter.api.Test;

public class LocalDateTest {

	@Test
	public void test() {
		LocalDateTime ldt = LocalDateTime.now();
		ldt = ldt.truncatedTo(ChronoUnit.SECONDS);
		
		System.out.println(ldt);
	}
}
