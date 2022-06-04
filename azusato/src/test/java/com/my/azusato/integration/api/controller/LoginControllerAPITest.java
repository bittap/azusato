package com.my.azusato.integration.api.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.my.azusato.common.TestConstant;
import com.my.azusato.common.TestConstant.Entity;
import com.my.azusato.config.SecurityConfig;
import com.my.azusato.entity.UserEntity;
import com.my.azusato.exception.AzusatoException;
import com.my.azusato.exception.ErrorResponse;
import com.my.azusato.integration.AbstractIntegration;
import com.my.azusato.login.LoginUser;

public class LoginControllerAPITest extends AbstractIntegration {

	final static String RESOUCE_BASIC_PATH = "src/test/data/integration/api/controller/";

	@Nested
	class Login {

		final static String RESOUCE_PATH = RESOUCE_BASIC_PATH + "login/";

		@ParameterizedTest
		@MethodSource("com.my.azusato.integration.api.controller.LoginControllerAPITest#thenRealtiveUser_resultOk")
		public void thenRealtiveUser_resultOk(String folderName, List<GrantedAuthority> expectedRoles) throws Exception {
			String userName = Entity.createdVarChars[0];
			
			dbUnitCompo.initalizeTable(Paths.get(RESOUCE_PATH, folderName, TestConstant.INIT_XML_FILE_NAME));

			mockMvc
					.perform(MockMvcRequestBuilders.post(SecurityConfig.API_LOGIN_URL)
							.param(SecurityConfig.USERNAME_PARAMETER, userName)
							.param(SecurityConfig.PASSWORD_PARAMETER, Entity.createdVarChars[1])
							.with(csrf()))
					.andDo(print())
					.andExpect(authenticated().withUsername(userName).withAuthorities(expectedRoles))
					.andExpect(status().isOk()).andReturn();
		}
		
		@ParameterizedTest
		@MethodSource("com.my.azusato.common.TestSource#locales")
		public void thenNotMatchPassword_result400(Locale locale) throws Exception {
			String userName = Entity.createdVarChars[0];
			String folderName = "5";
			
			dbUnitCompo.initalizeTable(Paths.get(RESOUCE_PATH, folderName, TestConstant.INIT_XML_FILE_NAME));
			MvcResult mvcResult = mockMvc
					.perform(MockMvcRequestBuilders.post(SecurityConfig.API_LOGIN_URL)
							.param(SecurityConfig.USERNAME_PARAMETER, userName)
							.param(SecurityConfig.PASSWORD_PARAMETER, "notMatchedPassword")
							.with(csrf())
							.locale(locale)
							)
					.andDo(print())
					.andExpect(status().isBadRequest()).andReturn();
			
			String resultBody = mvcResult.getResponse()
					.getContentAsString(Charset.forName(TestConstant.DEFAULT_CHARSET));
			ErrorResponse result = om.readValue(resultBody, ErrorResponse.class);

			assertEquals(new ErrorResponse(AzusatoException.I0010, messageSource.getMessage(AzusatoException.I0010, null, locale)),
					result);
		}
		
		@ParameterizedTest
		@MethodSource("com.my.azusato.common.TestSource#locales")
		public void thenNotFoundUser_result400(Locale locale) throws Exception {
			MvcResult mvcResult = mockMvc
					.perform(MockMvcRequestBuilders.post(SecurityConfig.API_LOGIN_URL)
							.param(SecurityConfig.USERNAME_PARAMETER, Entity.createdVarChars[0])
							.param(SecurityConfig.PASSWORD_PARAMETER, Entity.createdVarChars[1])
							.with(csrf())
							.locale(locale)
							)
					.andDo(print())
					.andExpect(status().isBadRequest()).andReturn();
			
			String resultBody = mvcResult.getResponse()
					.getContentAsString(Charset.forName(TestConstant.DEFAULT_CHARSET));
			ErrorResponse result = om.readValue(resultBody, ErrorResponse.class);

			assertEquals(new ErrorResponse(AzusatoException.I0009, messageSource.getMessage(AzusatoException.I0009, null, locale)),
					result);
		}

	}
	
	@Nested
	class Logout {

		final static String RESOUCE_PATH = RESOUCE_BASIC_PATH + "logout/";

		@Test
		public void thenLogout_resultOk() throws Exception {
			String userName = Entity.createdVarChars[0];
			String folderName = "1";
			
			dbUnitCompo.initalizeTable(Paths.get(RESOUCE_PATH, folderName, TestConstant.INIT_XML_FILE_NAME));

			// login
			mockMvc
					.perform(MockMvcRequestBuilders.post(SecurityConfig.API_LOGIN_URL)
							.param(SecurityConfig.USERNAME_PARAMETER, userName)
							.param(SecurityConfig.PASSWORD_PARAMETER, Entity.createdVarChars[1])
							.with(csrf()))
					.andDo(print())
					.andExpect(status().isOk()).andReturn();
			
			// logout
			mockMvc
			.perform(MockMvcRequestBuilders.post(SecurityConfig.API_LOGOUT_URL)
					.with(csrf())
					)
			.andDo(print())
			.andExpect(status().isOk())
			.andReturn();
		}
	}
	
	public static Stream<Arguments> thenRealtiveUser_resultOk(){
		return Stream.of(
					Arguments.of("1",AuthorityUtils.createAuthorityList(LoginUser.ROLE_PRIFIX+UserEntity.Type.admin.toString())),
					Arguments.of("2",AuthorityUtils.createAuthorityList(LoginUser.ROLE_PRIFIX+UserEntity.Type.kakao.toString())),
					Arguments.of("3",AuthorityUtils.createAuthorityList(LoginUser.ROLE_PRIFIX+UserEntity.Type.line.toString())),
					Arguments.of("4",AuthorityUtils.createAuthorityList(LoginUser.ROLE_PRIFIX+UserEntity.Type.nonmember.toString()))
				);
	}
}
