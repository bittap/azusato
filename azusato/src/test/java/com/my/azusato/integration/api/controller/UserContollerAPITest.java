package com.my.azusato.integration.api.controller;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.stream.Stream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.my.azusato.api.controller.UserControllerAPI;
import com.my.azusato.api.controller.request.AddNonMemberUserAPIRequest;
import com.my.azusato.api.service.response.GetSessionUserServiceAPIResponse;
import com.my.azusato.common.TestConstant;
import com.my.azusato.common.TestConstant.Entity;
import com.my.azusato.common.TestCookie;
import com.my.azusato.common.TestSession;
import com.my.azusato.entity.UserEntity;
import com.my.azusato.exception.AzusatoException;
import com.my.azusato.exception.ErrorResponse;
import com.my.azusato.integration.AbstractIntegration;
import com.my.azusato.integration.api.controller.CelebrationContollerAPITest.AddCelebration;
import com.my.azusato.view.controller.common.CookieConstant;
import com.my.azusato.view.controller.common.HttpConstant;
import com.my.azusato.view.controller.common.UrlConstant.Api;

public class UserContollerAPITest extends AbstractIntegration {
	
	final static String RESOUCE_BASIC_PATH = "src/test/data/integration/api/controller/";
	
	@Nested
	class getSessionUser {
		
		final static String RESOUCE_PATH = RESOUCE_BASIC_PATH + "getSessionUser/";
		
		@Test
		public void givenNormalCase_result200() throws Exception {
			dbUnitCompo.initalizeTable(Paths.get(AddCelebration.RESOUCE_PATH, "1", TestConstant.INIT_XML_FILE_NAME));
			
			MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders
						.get(TestConstant.MAKE_ABSOLUTE_URL + Api.COMMON_REQUSET+UserControllerAPI.COMMON_URL).session(TestSession.getAdminSession())
					).andDo(print()).andExpect(status().isOk()).andReturn();
			
			String resultStrBody = mvcResult.getResponse().getContentAsString(Charset.forName(TestConstant.DEFAULT_CHARSET));
			GetSessionUserServiceAPIResponse result = om.readValue(resultStrBody, GetSessionUserServiceAPIResponse.class);
			
			GetSessionUserServiceAPIResponse expect = GetSessionUserServiceAPIResponse.builder()
					.id(Entity.createdVarChars[0])
					.name(Entity.createdVarChars[2])
					.profileImageBase64(Entity.createdVarChars[0])
					.profileImageType(Entity.ImageType[0])
					.build();
			
			Assertions.assertEquals(expect, result);
		}
		
		@ParameterizedTest
		@MethodSource("com.my.azusato.common.TestSource#locales")
		public void givenNotSession_result400(Locale locale) throws Exception {
			MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders
					.get(TestConstant.MAKE_ABSOLUTE_URL + Api.COMMON_REQUSET+UserControllerAPI.COMMON_URL)
					.locale(locale)
				).andDo(print()).andExpect(status().isBadRequest()).andReturn();
		
			String resultStrBody = mvcResult.getResponse().getContentAsString(Charset.forName(TestConstant.DEFAULT_CHARSET));
			ErrorResponse result = om.readValue(resultStrBody, ErrorResponse.class);
			
			ErrorResponse expect = new ErrorResponse(AzusatoException.I0002,
					messageSource.getMessage(AzusatoException.I0002, null, locale));
			
			Assertions.assertEquals(expect, result);
		}
		
		@ParameterizedTest
		@MethodSource("com.my.azusato.common.TestSource#locales")
		public void givenNotExistData_result500(Locale locale) throws Exception {
			MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders
					.get(TestConstant.MAKE_ABSOLUTE_URL + Api.COMMON_REQUSET+UserControllerAPI.COMMON_URL).session(TestSession.getAdminSession())
					.locale(locale)
				).andDo(print()).andExpect(status().isBadRequest()).andReturn();
		
			String resultStrBody = mvcResult.getResponse().getContentAsString(Charset.forName(TestConstant.DEFAULT_CHARSET));
			ErrorResponse result = om.readValue(resultStrBody, ErrorResponse.class);
			
			String tableName = messageSource.getMessage(UserEntity.TABLE_NAME_KEY, null, locale);
			ErrorResponse expect = new ErrorResponse(AzusatoException.I0005,messageSource.getMessage(AzusatoException.I0005, new String[] { tableName }, locale));
			
			Assertions.assertEquals(expect, result);
		}
	}


	@Nested
	class AddNonMember {

		@ParameterizedTest
		@MethodSource("com.my.azusato.integration.api.controller.UserContollerAPITest#givenVaildParameter_resultOk")
		public void givenVaildParameter_resultOk(AddNonMemberUserAPIRequest req) throws Exception {
			String requestBody = om.writeValueAsString(req);
			
			mockMvc.perform(MockMvcRequestBuilders
					.post(TestConstant.MAKE_ABSOLUTE_URL + Api.COMMON_REQUSET + UserControllerAPI.ADD_NONMEMBER_URL)
					.content(requestBody).contentType(HttpConstant.DEFAULT_CONTENT_TYPE_STRING)).andDo(print())
					.andExpect(status().isCreated())
					.andExpect(cookie().value(CookieConstant.NON_MEMBER_KEY, notNullValue()));
		}

		@ParameterizedTest
		@MethodSource("com.my.azusato.common.TestSource#locales")
		public void semi_abnormal_case(Locale locale) throws Exception {
			AddNonMemberUserAPIRequest req = AddNonMemberUserAPIRequest.builder().name(Entity.createdVarChars[0])
			.profileImageType("image/png").profileImageBase64(Entity.createdVarChars[2]).build();
			String requestBody = om.writeValueAsString(req);
			
			MvcResult mvcResult = mockMvc
					.perform(MockMvcRequestBuilders
							.post(TestConstant.MAKE_ABSOLUTE_URL + Api.COMMON_REQUSET + UserControllerAPI.ADD_NONMEMBER_URL)
							.locale(locale).contentType(HttpConstant.DEFAULT_CONTENT_TYPE_STRING)
							.content(requestBody)
							.cookie(TestCookie.getNonmemberCookie()).contentType(HttpConstant.DEFAULT_CONTENT_TYPE_STRING))
					.andDo(print()).andExpect(status().isBadRequest()).andReturn();

			String resultBody = mvcResult.getResponse()
					.getContentAsString(Charset.forName(TestConstant.DEFAULT_CHARSET));
			ErrorResponse result = om.readValue(resultBody, ErrorResponse.class);

			assertEquals(new ErrorResponse(AzusatoException.I0003, messageSource.getMessage(AzusatoException.I0003, null, locale)),
					result);
		}
		
		@ParameterizedTest
		@MethodSource("com.my.azusato.integration.api.controller.UserContollerAPITest#givenInvaildParameter_result400")
		public void givenInvaildParameter_result400(Locale locale, AddNonMemberUserAPIRequest req, String expectedMessage)
				throws Exception {
			String requestBody = om.writeValueAsString(req);
			MvcResult mvcResult = mockMvc
					.perform(MockMvcRequestBuilders
							.post(TestConstant.MAKE_ABSOLUTE_URL + Api.COMMON_REQUSET + UserControllerAPI.ADD_NONMEMBER_URL)
							.content(requestBody).contentType(HttpConstant.DEFAULT_CONTENT_TYPE_STRING).locale(locale))
					.andDo(print()).andExpect(status().is(400)).andReturn();

			String resultBody = mvcResult.getResponse()
					.getContentAsString(Charset.forName(TestConstant.DEFAULT_CHARSET));
			ErrorResponse result = om.readValue(resultBody, ErrorResponse.class);

			assertEquals(new ErrorResponse(AzusatoException.I0004, expectedMessage), result);
		}
	}
	
	@SuppressWarnings("unused")
	private static Stream<Arguments> givenVaildParameter_resultOk() {
		return Stream.of(
				Arguments.of(AddNonMemberUserAPIRequest.builder().name(Entity.createdVarChars[0])
						.profileImageType("image/png").profileImageBase64(Entity.createdVarChars[2]).build()),
				Arguments.of(AddNonMemberUserAPIRequest.builder().name(Entity.createdVarChars[0])
						.profileImageType("image/jpeg").profileImageBase64(Entity.createdVarChars[2]).build())
				);
	}
	
	@SuppressWarnings("unused")
	private static Stream<Arguments> givenInvaildParameter_result400() {
		return Stream.of(
				Arguments.of(TestConstant.LOCALE_JA,
						AddNonMemberUserAPIRequest.builder().profileImageType("image/svg+xml").build(),
						"プロフィールイメージはpng、jpegのみ可能です。\n名前は必修項目です。"),
				Arguments.of(TestConstant.LOCALE_KO,
						AddNonMemberUserAPIRequest.builder().profileImageType("image/svg+xml").build(),
						"이름을 입력해주세요.\n프로필사진은 png, jpeg만 지원됩니다.")
				);
	}
}
