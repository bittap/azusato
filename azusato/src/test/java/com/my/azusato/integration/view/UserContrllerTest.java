package com.my.azusato.integration.view;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.servlet.ModelAndView;

import com.my.azusato.common.TestConstant;
import com.my.azusato.integration.AbstractIntegrationForTest;
import com.my.azusato.view.controller.UserController;
import com.my.azusato.view.controller.common.ModelConstant;
import com.my.azusato.view.controller.common.UrlConstant;
import com.my.azusato.view.controller.response.HeaderReponse;

public class UserContrllerTest extends AbstractIntegrationForTest {
	
	private final int MODEL_SIZE = 1 + TestConstant.SPRING_MODEL_SIZE;

	@Nested
	public class Login {

		@ParameterizedTest
		@ValueSource(strings = { UrlConstant.USER_CONTROLLER_REQUSET + UserController.USER_LOGIN_URL,
				UrlConstant.JAPANESE_CONTROLLER_REQUEST + UrlConstant.USER_CONTROLLER_REQUSET + UserController.USER_LOGIN_URL,
				UrlConstant.KOREAN_CONTROLLER_REQUEST + UrlConstant.USER_CONTROLLER_REQUSET + UserController.USER_LOGIN_URL })
		public void givenUrl_result200(String url) throws Exception {
			MvcResult resultOfMvc = mockMvc.perform(MockMvcRequestBuilders.get(url)).andExpect(status().is(200))
					.andReturn();

			ModelAndView mavOfResult = resultOfMvc.getModelAndView();

			Map<String, Object> mapsOfResult = mavOfResult.getModel();
			mapsOfResult.forEach((k, v) -> {
				System.out.printf("key : %s, value : %s\n", k, v);
			});

			Assertions.assertEquals(MODEL_SIZE, mapsOfResult.size());

			HeaderReponse headerOfResult = (HeaderReponse) mapsOfResult.get(ModelConstant.HEADER_KEY);

			HeaderReponse expect = new HeaderReponse();
			expect.setUser(true);

			Assertions.assertEquals(expect, headerOfResult);
		}
	}
}
