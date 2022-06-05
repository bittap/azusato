package com.my.azusato.integration.view;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Stream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.servlet.ModelAndView;

import com.my.azusato.common.TestConstant;
import com.my.azusato.common.TestLogin;
import com.my.azusato.integration.AbstractIntegration;
import com.my.azusato.locale.LocaleConstant;
import com.my.azusato.login.LoginUser;
import com.my.azusato.view.controller.common.ModelConstant;
import com.my.azusato.view.controller.common.UrlConstant;
import com.my.azusato.view.controller.response.HeaderReponse;

public class IndexControllerTest extends AbstractIntegration {

	private final int MODEL_SIZE = 1 + TestConstant.SPRING_MODEL_SIZE;

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
			mapsOfResult.forEach((k, v) -> {
				System.out.printf("key : %s, value : %s\n", k, v);
			});

			Assertions.assertEquals(MODEL_SIZE, mapsOfResult.size());

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
									.with(user(TestLogin.adminLoginUser(grant)))
								)
						.andExpect(status().isFound())
						.andExpect(redirectedUrl(expectedRedirectUrl))
					.andReturn();
		}
		
		// @ParameterizedTestはGrantがstaticでは取得できないため@Testに変更。
		@Test
		public void givenNotAdminLogin_resultNotRedicrt() throws Exception {
			
			List<LoginUser> loginUsers = List.of(TestLogin.lineLoginUser(grant),TestLogin.kakaoLoginUser(grant),TestLogin.nonmemberLoginUser(grant));
			
			for (LoginUser loginUser : loginUsers) {
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
				mapsOfResult.forEach((k, v) -> {
					System.out.printf("key : %s, value : %s\n", k, v);
				});

				Assertions.assertEquals(MODEL_SIZE, mapsOfResult.size());

				HeaderReponse headerOfResult = (HeaderReponse) mapsOfResult.get(ModelConstant.HEADER_KEY);

				HeaderReponse expect = new HeaderReponse();
				expect.setHome(true);

				Assertions.assertEquals(expect, headerOfResult);
			}
		}
	}
	
	public static Stream<Arguments> givenAdminLogin_resultRedicrt(){
		
		return Stream.of(
					Arguments.of(LocaleConstant.supportedLocales.get(0),UrlConstant.JAPANESE_CONTROLLER_REQUEST + UrlConstant.COMMON_ADMIN_CONTROLLER_REQUSET),
					Arguments.of(LocaleConstant.supportedLocales.get(1),UrlConstant.KOREAN_CONTROLLER_REQUEST + UrlConstant.COMMON_ADMIN_CONTROLLER_REQUSET)
				);
				
	}
}
