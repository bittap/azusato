package com.my.azusato.integration.api.controller;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.my.azusato.api.controller.ProfileControllerAPI;
import com.my.azusato.api.controller.response.DefaultRandomProfileResponse;
import com.my.azusato.common.TestConstant;
import com.my.azusato.integration.AbstractIntegration;
import com.my.azusato.view.controller.common.UrlConstant.Api;

public class ProfileControllerAPITest extends AbstractIntegration {

	@Nested
	class RandomProfile {

		/**
		 * random基本イメージを比較するために、宣言 key : base64 , value : false マッチするとfalseからtrueに変更する。
		 */
		Map<String, Boolean> expects = expect();

		@Test
		public void normal_case() throws Exception {
			MvcResult mvcResult = mockMvc
					.perform(MockMvcRequestBuilders.get(TestConstant.MAKE_ABSOLUTE_URL +  Api.COMMON_REQUSET +  ProfileControllerAPI.RANDOM_URL))
					.andDo(print()).andExpect(status().isOk()).andReturn();

			DefaultRandomProfileResponse result = om.readValue(mvcResult.getResponse().getContentAsString(),
					DefaultRandomProfileResponse.class);

			assertTrue(expects.containsKey(result.getProfileImagePath()));
		}
	}
	
	public Map<String, Boolean> expect(){
		Path path = Paths.get("src","main","resources","static","image","default","profile");
		System.out.printf("absolutPath: %s\n",path.toAbsolutePath());
		List<String> fileNames = Arrays.asList(path.toFile().list());

		return fileNames.stream().collect(Collectors.toMap((e)->{
			return Paths.get(profileProperty.getClientDefaultImageFolderPath(),e).toString();
		}, (e)->{
			return Boolean.valueOf(false);
		}));

	}
}
