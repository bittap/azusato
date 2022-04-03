package com.my.azusato;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.my.azusato.common.TestConstant;

@SpringBootTest
@ActiveProfiles(TestConstant.PROFILES)
class AzusatoApplicationTests {

	@Test
	void contextLoads() {
	}

}
