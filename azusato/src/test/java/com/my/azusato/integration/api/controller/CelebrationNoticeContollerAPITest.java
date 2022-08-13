package com.my.azusato.integration.api.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.my.azusato.common.TestConstant;
import com.my.azusato.common.TestLogin;
import com.my.azusato.exception.ErrorResponse;
import com.my.azusato.integration.AbstractIntegration;

public class CelebrationNoticeContollerAPITest extends AbstractIntegration {

	final static String RESOUCE_BASIC_PATH = "src/test/data/integration/CelebrationNoticeContollerAPI/";
	
	static final String COMMON_URL = "celebration-notice";
	
	
	@Nested
	class celebrationNotices {
		
		final static String RESOUCE_PATH = RESOUCE_BASIC_PATH + "celebrationNotices/";
		
		final String URL = COMMON_URL + "s";
		
		@Test
		@DisplayName("正常系_結果200")
		public void givenNoraml_result200() throws Exception {
			String folderName = "1";
			Path initFilePath = Paths.get(RESOUCE_PATH, folderName, TestConstant.INIT_XML_FILE_NAME);
			dbUnitCompo.initalizeTable(initFilePath);

			mockMvc.perform(MockMvcRequestBuilders
					.get(TestConstant.MAKE_ABSOLUTE_URL + TestConstant.API_REQUEST_URL + URL)
					.with(csrf())
					.with(user(TestLogin.adminLoginUser())))
					.andDo(print())
					.andExpect(status().isOk());

		}
		
		@DisplayName("準正常系_ログインしていない_結果401")
		@ParameterizedTest
		@MethodSource("com.my.azusato.common.TestSource#givenNoLogin_result401")
		public void givenNoLogin_result401(Locale locale, String expectMssage) throws Exception {
			MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders
					.get(TestConstant.MAKE_ABSOLUTE_URL + TestConstant.API_REQUEST_URL + URL)
					.with(csrf())
					.locale(locale))
					.andDo(print())
					.andExpect(status().is(401))
					.andReturn();

			String resultBody = mvcResult.getResponse()
					.getContentAsString(Charset.forName(TestConstant.DEFAULT_CHARSET));
			ErrorResponse result = om.readValue(resultBody, ErrorResponse.class);

			assertEquals(new ErrorResponse("I-0001", expectMssage),result);
		}
	}

	@Nested
	class read {

		final static String RESOUCE_PATH = RESOUCE_BASIC_PATH + "read/";
		
		final String CELEBRATION_NO = "1";
		
		final String READCOUNTUP_URL = COMMON_URL + "/read/" + CELEBRATION_NO;

		@Test
		@DisplayName("正常系_結果200")
		public void givenNoraml_result200() throws Exception {
			String folderName = "1";
			Path initFilePath = Paths.get(RESOUCE_PATH, folderName, TestConstant.INIT_XML_FILE_NAME);
			dbUnitCompo.initalizeTable(initFilePath);

			mockMvc.perform(MockMvcRequestBuilders
					.put(TestConstant.MAKE_ABSOLUTE_URL + TestConstant.API_REQUEST_URL + READCOUNTUP_URL)
					.with(csrf())
					.with(user(TestLogin.adminLoginUser()))).andDo(print())
					.andExpect(status().isOk());

		}
		
		@DisplayName("準正常系_ログインしていない_結果401")
		@ParameterizedTest
		@MethodSource("com.my.azusato.common.TestSource#givenNoLogin_result401")
		public void givenNoLogin_result401(Locale locale, String expectMssage) throws Exception {
			MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders
					.put(TestConstant.MAKE_ABSOLUTE_URL + TestConstant.API_REQUEST_URL + READCOUNTUP_URL)
					.with(csrf())
					.locale(locale))
					.andDo(print())
					.andExpect(status().is(401))
					.andReturn();

			String resultBody = mvcResult.getResponse()
					.getContentAsString(Charset.forName(TestConstant.DEFAULT_CHARSET));
			ErrorResponse result = om.readValue(resultBody, ErrorResponse.class);

			assertEquals(new ErrorResponse("I-0001", expectMssage),result);
		}
	}
}
