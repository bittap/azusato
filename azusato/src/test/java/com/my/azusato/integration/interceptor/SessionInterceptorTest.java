package com.my.azusato.integration.interceptor;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;

import javax.servlet.http.Cookie;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.my.azusato.common.TestConstant;
import com.my.azusato.common.TestCookie;
import com.my.azusato.common.TestSession;
import com.my.azusato.dto.LoginUserDto;
import com.my.azusato.entity.UserEntity.Type;
import com.my.azusato.integration.AbstractIntegration;
import com.my.azusato.view.controller.common.SessionConstant;

public class SessionInterceptorTest extends AbstractIntegration {

	private final String NOT_EXIST_URL = "test";
	
	@Nested
	class PreHandle{
		
		@Test
		public void givenSession_resultSessionRefrashed_normal_case() throws Exception {
			int shortIntervalSessionTime = 1;
			MockHttpSession session = TestSession.getAdminSession();
			session.setMaxInactiveInterval(shortIntervalSessionTime);
			MvcResult mvcResult = mockMvc.perform(
						MockMvcRequestBuilders.get(TestConstant.MAKE_ABSOLUTE_URL + NOT_EXIST_URL).session(session)
					)
					.andDo(print()).andExpect(request().sessionAttribute(SessionConstant.LOGIN_KEY, session.getAttribute(SessionConstant.LOGIN_KEY))).andReturn();
			
			int sessionMaxInterval = mvcResult.getRequest().getSession().getMaxInactiveInterval();

			assertThat("check if session is refreshed",sessionMaxInterval,Matchers.greaterThan(shortIntervalSessionTime));
			
		}
		
		@Test
		public void givenNonmemberCookie_resultSessionExist_normal_case() throws Exception {
			Cookie cookie = TestCookie.getNonmemberCookie();
			mockMvc.perform(
						MockMvcRequestBuilders.get(TestConstant.MAKE_ABSOLUTE_URL + NOT_EXIST_URL).cookie(cookie)
					)
					.andDo(print()).andExpect(request().sessionAttribute(SessionConstant.LOGIN_KEY, new LoginUserDto(1L, Type.nonmember.toString())));
			
		}
		
		@Test
		public void givenEtcCookie_resultSessionNotExist_normal_case() throws Exception {
			Cookie cookie = new Cookie("etc", "etc");
			mockMvc.perform(
						MockMvcRequestBuilders.get(TestConstant.MAKE_ABSOLUTE_URL + NOT_EXIST_URL).cookie(cookie)
					)
					.andDo(print()).andExpect(request().sessionAttributeDoesNotExist(SessionConstant.LOGIN_KEY));
			
		}
		
		@Test
		public void givenNoting_resultNotExistEssion_normal_case() throws Exception {
			mockMvc.perform(
					MockMvcRequestBuilders.get(TestConstant.MAKE_ABSOLUTE_URL + NOT_EXIST_URL)
				)
				.andDo(print()).andExpect(request().sessionAttributeDoesNotExist(SessionConstant.LOGIN_KEY));
			
		}
	}
}
