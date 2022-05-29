package com.my.azusato.integration.api.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.my.azusato.api.controller.ProfileControllerAPI;
import com.my.azusato.api.controller.response.DefaultRandomProfileResponse;
import com.my.azusato.common.TestConstant;
import com.my.azusato.integration.AbstractIntegration;
import com.my.azusato.property.ProfileProperty;
import com.my.azusato.view.controller.common.UrlConstant.Api;

public class ProfileControllerAPITest extends AbstractIntegration {

	@Autowired
	private ProfileProperty profileProperty;

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

		@Test
		public void normal_case() throws Exception {
			MvcResult mvcResult = mockMvc
					.perform(MockMvcRequestBuilders.get(TestConstant.MAKE_ABSOLUTE_URL +  Api.COMMON_REQUSET +  ProfileControllerAPI.RANDOM_URL))
					.andDo(print()).andExpect(status().isOk()).andReturn();

			DefaultRandomProfileResponse result = om.readValue(mvcResult.getResponse().getContentAsString(),
					DefaultRandomProfileResponse.class);

			assertEquals(profileProperty.getDefaultImageType(), result.getProfileImageType());
			assertTrue(expects.containsKey(result.getProfileImageBase64()));
		}
	}
}
