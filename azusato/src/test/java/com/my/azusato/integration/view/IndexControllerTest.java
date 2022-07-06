package com.my.azusato.integration.view;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Locale;
import java.util.Map;
import java.util.stream.Stream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.servlet.ModelAndView;

import com.my.azusato.common.TestLogin;
import com.my.azusato.integration.AbstractIntegration;
import com.my.azusato.locale.LocaleConstant;
import com.my.azusato.login.LoginUser;
import com.my.azusato.view.controller.common.ModelConstant;
import com.my.azusato.view.controller.common.UrlConstant;
import com.my.azusato.view.controller.response.HeaderReponse;

public class IndexControllerTest extends AbstractIntegration {

	@Nested
	public class Status_200 {

		/**
		 * Check 200 status,model size and model values
		 */
		@ParameterizedTest
		@ValueSource(strings = { UrlConstant.INDEX_CONTROLLER_REQUSET, UrlConstant.JAPANESE_CONTROLLER_REQUEST,
				UrlConstant.KOREAN_CONTROLLER_REQUEST })
		public void givenNonlogin_resultNotRedicrt(String url) throws Exception {
			MvcResult resultOfMvc = mockMvc.perform(MockMvcRequestBuilders.get(url)).andExpect(status().is(200))
					.andReturn();

			ModelAndView mavOfResult = resultOfMvc.getModelAndView();

			Map<String, Object> mapsOfResult = mavOfResult.getModel();

			HeaderReponse headerOfResult = (HeaderReponse) mapsOfResult.get(ModelConstant.HEADER_KEY);

			HeaderReponse expect = new HeaderReponse();
			expect.setHome(true);

			Assertions.assertEquals(expect, headerOfResult);
		}
		
		@ParameterizedTest
		@MethodSource("com.my.azusato.integration.view.IndexControllerTest#givenAdminLogin_resultRedicrt")
		public void givenAdminLogin_resultRedicrt(Locale locale, String expectedRedirectUrl) throws Exception {
			mockMvc
						.perform(
								MockMvcRequestBuilders
									.get(UrlConstant.INDEX_CONTROLLER_REQUSET)
									.locale(locale)
									.with(user(TestLogin.adminLoginUser()))
								)
						.andExpect(status().isFound())
						.andExpect(redirectedUrl(expectedRedirectUrl))
					.andReturn();
		}
		
		@ParameterizedTest
		@MethodSource("com.my.azusato.common.TestSource#notAdminLogin")
		public void givenNotAdminLogin_resultNotRedicrt(LoginUser loginUser) throws Exception {
			
			MvcResult resultOfMvc = mockMvc
					.perform(
							MockMvcRequestBuilders
								.get(UrlConstant.INDEX_CONTROLLER_REQUSET)
								.with(user(loginUser))
							)
					.andExpect(status().is(200))
					.andReturn();

			ModelAndView mavOfResult = resultOfMvc.getModelAndView();

			Map<String, Object> mapsOfResult = mavOfResult.getModel();

			HeaderReponse headerOfResult = (HeaderReponse) mapsOfResult.get(ModelConstant.HEADER_KEY);

			HeaderReponse expect = new HeaderReponse();
			expect.setHome(true);

			Assertions.assertEquals(expect, headerOfResult);
		}
	}
	
	public static Stream<Arguments> givenAdminLogin_resultRedicrt(){
		
		return Stream.of(
					Arguments.of(LocaleConstant.supportedLocales.get(0),UrlConstant.JAPANESE_CONTROLLER_REQUEST + UrlConstant.COMMON_ADMIN_CONTROLLER_REQUSET),
					Arguments.of(LocaleConstant.supportedLocales.get(1),UrlConstant.KOREAN_CONTROLLER_REQUEST + UrlConstant.COMMON_ADMIN_CONTROLLER_REQUSET)
				);
				
	}
}
