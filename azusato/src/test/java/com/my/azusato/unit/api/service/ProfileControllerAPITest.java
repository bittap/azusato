package com.my.azusato.unit.api.service;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.util.ReflectionTestUtils;

import com.my.azusato.api.controller.ProfileControllerAPI;
import com.my.azusato.integration.AbstractIntegration;
import com.my.azusato.property.ProfileProperty;

public class ProfileControllerAPITest extends AbstractIntegration {

	
	@Autowired
	private ProfileProperty profileProperty;
	@Autowired
	private ProfileControllerAPI profileControllerAPI;

	@Nested
	class RandomProfile {

		/**
		 * random基本イメージを比較するために、宣言 key : base64 , value : false マッチするとfalseからtrueに変更する。
		 */
		Map<String, Boolean> expects;

		@BeforeEach
		public void beforeEach() throws Exception {
			expects = new HashMap<>();
			for (int i = 1; i <= profileProperty.getDefaultMaxNumber(); i++) {
				Path path = Paths.get("src/main/resources/default/profile/base64", "avatar" + i + ".txt");
				expects.put(Files.readString(path), false);
			}
		}

		/**
		 * およそ10000回を実行し、もし引っかからないものがあればfailにする。
		 * 
		 * @throws Exception
		 */
		@Test
		public void getDefaultProfileBase64_normal_case() throws Exception {
			int threadCount = 10;
			ExecutorService executor = Executors.newFixedThreadPool(threadCount);

			for (int i = 0; i < threadCount; i++) {
				executor.submit(() -> {
					for (int j = 0; j < 1000; j++) {
						String result = (String) ReflectionTestUtils.invokeMethod(profileControllerAPI,
								"getDefaultProfileBase64");
						if (expects.containsKey(result)) {
							expects.replace(result, true);
						}
					}
				});
			}
			// 作業が完了すると終了する。
			executor.shutdown();
			// 待機
			executor.awaitTermination(30, TimeUnit.SECONDS);

			expects.forEach((k, v) -> {
				assertTrue(v);
			});

		}
	}
}
