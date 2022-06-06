package com.my.azusato.integration.interceptor;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;

import java.nio.file.Paths;

import javax.servlet.http.Cookie;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.util.NestedServletException;

import com.my.azusato.common.TestConstant;
import com.my.azusato.common.TestCookie;
import com.my.azusato.common.TestLogin;
import com.my.azusato.integration.AbstractIntegration;
import com.my.azusato.login.LoginUser;

public class LoginInterceptorTest extends AbstractIntegration {

	private final String NOT_EXIST_URL = "test";
	
	final static String RESOUCE_BASIC_PATH = "src/test/data/integration/interceptor/loginInterceptor";
	
	@Nested
	class PreHandle{
		
		@Test
		public void givenSession_resultSessionRefrashed() throws Exception {
			int shortIntervalSessionTime = 1;
			MockHttpSession session = new MockHttpSession();
			session.setMaxInactiveInterval(shortIntervalSessionTime);
			LoginUser loginUser = TestLogin.adminLoginUser();
			MvcResult mvcResult = mockMvc.perform(
						MockMvcRequestBuilders.get(TestConstant.MAKE_ABSOLUTE_URL + NOT_EXIST_URL)
						.with(user(loginUser))
					)
					.andDo(print()).andExpect(authenticated().withUsername(loginUser.getUsername()).withAuthorities(loginUser.getAuthorities())).andReturn();
			
			int sessionMaxInterval = mvcResult.getRequest().getSession().getMaxInactiveInterval();

			assertThat("check if session is refreshed",sessionMaxInterval,Matchers.greaterThan(shortIntervalSessionTime));
			
		}
		
		@Test
		public void givenNonmemberCookie_resultSessionExist() throws Exception {
			String folderName = "1";
			
			dbUnitCompo.initalizeTable(Paths.get(RESOUCE_BASIC_PATH, folderName, TestConstant.INIT_XML_FILE_NAME));
			
			LoginUser loginUser = TestLogin.nonmemberLoginUser();
			Cookie cookie = TestCookie.getNonmemberCookie();
			mockMvc.perform(
						MockMvcRequestBuilders.get(TestConstant.MAKE_ABSOLUTE_URL + NOT_EXIST_URL).cookie(cookie)
					)
					.andDo(print()).andExpect(authenticated().withUsername(loginUser.getUsername()).withAuthorities(loginUser.getAuthorities()));	
		}
		
		@Test
		public void givenUserTypeAdminCookie_resultNullPointerException() throws Exception {
			String folderName = "2";
			
			dbUnitCompo.initalizeTable(Paths.get(RESOUCE_BASIC_PATH, folderName, TestConstant.INIT_XML_FILE_NAME));
			
			Cookie cookie = TestCookie.getNonmemberCookie();
			
			org.junit.jupiter.api.Assertions.assertThrows(NestedServletException.class, ()->{
				mockMvc.perform(
						MockMvcRequestBuilders.get(TestConstant.MAKE_ABSOLUTE_URL + NOT_EXIST_URL).cookie(cookie)
					)
					.andDo(print());
			});
			
			
		}
		
		@Test
		public void givenEtcCookie_resultSessionNotExist() throws Exception {
			Cookie cookie = new Cookie("etc", "etc");
			mockMvc.perform(
						MockMvcRequestBuilders.get(TestConstant.MAKE_ABSOLUTE_URL + NOT_EXIST_URL).cookie(cookie)
					)
					.andDo(print()).andExpect(request().sessionAttributeDoesNotExist(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY));
			
		}
		
		@Test
		public void givenNoting_resultNotExistEssion() throws Exception {
			mockMvc.perform(
					MockMvcRequestBuilders.get(TestConstant.MAKE_ABSOLUTE_URL + NOT_EXIST_URL)
				)
				.andDo(print()).andExpect(request().sessionAttributeDoesNotExist(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY));
			
		}
	}
}
