package com.my.azusato.integration.api.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;

import javax.servlet.http.Cookie;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.my.azusato.api.controller.CelebrationControllerAPI;
import com.my.azusato.api.controller.CelebrationReplyControllerAPI;
import com.my.azusato.api.controller.request.AddCelebrationAPIReqeust;
import com.my.azusato.common.TestConstant;
import com.my.azusato.common.TestConstant.Entity;
import com.my.azusato.common.TestSession;
import com.my.azusato.entity.CelebrationReplyEntity;
import com.my.azusato.exception.AzusatoException;
import com.my.azusato.exception.ErrorResponse;
import com.my.azusato.integration.AbstractIntegration;
import com.my.azusato.property.ProfileProperty;
import com.my.azusato.view.controller.common.HttpConstant;
import com.my.azusato.view.controller.common.UrlConstant.Api;

public class CelebrationReplyContollerAPITest extends AbstractIntegration {

	final static String RESOUCE_BASIC_PATH = "src/test/data/integration/api/controller/";
	
	@Autowired
	private ProfileProperty profileProperty;

	@Nested
	class AddCelebration {

		final static String RESOUCE_PATH = RESOUCE_BASIC_PATH + "addCelebration/";

		@ParameterizedTest
		@MethodSource("com.my.azusato.integration.api.controller.CelebrationContollerAPITest#addCelebration_normal_case")
		public void normal_case_admin(MockHttpSession session, Cookie cookie, Path initFilePath, Path expectFilePath,
				String[] comparedTables) throws Exception {
			dbUnitCompo.initalizeTable(initFilePath);

			mockMvc.perform(MockMvcRequestBuilders
					.post(TestConstant.MAKE_ABSOLUTE_URL + Api.COMMON_REQUSET + CelebrationControllerAPI.COMMON_URL)
					.content(getRequestBody()).contentType(HttpConstant.DEFAULT_CONTENT_TYPE_STRING).session(session)
					.cookie(cookie)).andDo(print()).andExpect(status().isCreated());

			// compare tables
			for (String table : comparedTables) {
				// exclude to compare dateTime columns when celebration table
				if (table.equals("celebration")) {
					dbUnitCompo.compareTable(expectFilePath, table, TestConstant.DEFAULT_EXCLUDE_COLUMNS);
				} else if (table.equals("celebration_notice")) {
					dbUnitCompo.compareTable(expectFilePath, table, new String[] { "celebration_no" });
				} else if(table.equals("user") || table.equals("profile")) {
					dbUnitCompo.compareTable(expectFilePath, table, TestConstant.DEFAULT_EXCLUDE_DATE_COLUMNS);
				}else {
					dbUnitCompo.compareTable(expectFilePath, table);
				}
			}
		}

		@ParameterizedTest
		@MethodSource("com.my.azusato.common.TestSource#locales")
		public void abnormal_case(Locale locale) throws Exception {

			MvcResult mvcResult = mockMvc
					.perform(MockMvcRequestBuilders
							.post(TestConstant.MAKE_ABSOLUTE_URL + Api.COMMON_REQUSET + CelebrationControllerAPI.COMMON_URL)
							.content(getRequestBody()).contentType(HttpConstant.DEFAULT_CONTENT_TYPE_STRING)
							.locale(locale))
					.andDo(print()).andExpect(status().is(401)).andReturn();

			String resultBody = mvcResult.getResponse()
					.getContentAsString(Charset.forName(TestConstant.DEFAULT_CHARSET));
			ErrorResponse result = om.readValue(resultBody, ErrorResponse.class);

			assertEquals(new ErrorResponse(AzusatoException.I0001, messageSource.getMessage(AzusatoException.I0001, null, locale)),
					result);
		}

		@ParameterizedTest
		@MethodSource("com.my.azusato.integration.api.controller.CelebrationContollerAPITest#addCelebration_semi_abnormal_case")
		public void semi_abnormal_case(Locale locale, AddCelebrationAPIReqeust req, String expectedMessage)
				throws Exception {
			String requestBody = om.writeValueAsString(req);
			MvcResult mvcResult = mockMvc
					.perform(MockMvcRequestBuilders
							.post(TestConstant.MAKE_ABSOLUTE_URL + Api.COMMON_REQUSET + CelebrationControllerAPI.COMMON_URL)
							.content(requestBody).contentType(HttpConstant.DEFAULT_CONTENT_TYPE_STRING).locale(locale))
					.andDo(print()).andExpect(status().is(400)).andReturn();

			String resultBody = mvcResult.getResponse()
					.getContentAsString(Charset.forName(TestConstant.DEFAULT_CHARSET));
			ErrorResponse result = om.readValue(resultBody, ErrorResponse.class);

			assertEquals(new ErrorResponse(AzusatoException.I0004, expectedMessage), result);
		}

		private String getRequestBody() throws Exception {
			AddCelebrationAPIReqeust req = AddCelebrationAPIReqeust.builder()
					.name(Entity.updatedVarChars[0]).profileImageBase64(Entity.updatedVarChars[1]).profileImageType(profileProperty.getDefaultImageType())
					.title(Entity.createdVarChars[0]).content(Entity.createdVarChars[1]).build();

			return om.writeValueAsString(req);
		}
	}
	
	
	@Nested
	class DeleteCeleReplybration {

		final static String RESOUCE_PATH = RESOUCE_BASIC_PATH + "deleteCelebration-reply/";
		
		final String CELEBRATION_REPLY_NO = "1";

		@Test
		public void givenNoraml_result200() throws Exception {
			String folderName = "1";
			Path initFilePath = Paths.get(DeleteCeleReplybration.RESOUCE_PATH, folderName, TestConstant.INIT_XML_FILE_NAME);
			Path expectFilePath = Paths.get(DeleteCeleReplybration.RESOUCE_PATH, folderName, TestConstant.EXPECT_XML_FILE_NAME);
			String[] COMPARED_TABLE_NAME = { "user", "celebration", "profile" , "celebration_reply"};
			
			dbUnitCompo.initalizeTable(initFilePath);

			mockMvc.perform(MockMvcRequestBuilders
					.delete(TestConstant.MAKE_ABSOLUTE_URL + Api.COMMON_REQUSET + CelebrationReplyControllerAPI.COMMON_URL + "/" + CELEBRATION_REPLY_NO)
					.session(TestSession.getAdminSession())).andDo(print()).andExpect(status().isOk());

			// compare tables
			for (String table : COMPARED_TABLE_NAME) {
				// exclude to compare dateTime columns when celebration table
				if(table.equals("user")) {
					dbUnitCompo.compareTable(expectFilePath, table);
				} else {
					dbUnitCompo.compareTable(expectFilePath, table,
							TestConstant.DEFAULT_EXCLUDE_UPDATE_DATE_COLUMNS);
				}
			}
		}
		
		@ParameterizedTest
		@MethodSource("com.my.azusato.common.TestSource#locales")
		public void givenDifferenceUser_result400(Locale locale) throws Exception {
			Path initFilePath = Paths.get(DeleteCeleReplybration.RESOUCE_PATH, "2", TestConstant.INIT_XML_FILE_NAME);
			dbUnitCompo.initalizeTable(initFilePath);
			
			MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders
					.delete(TestConstant.MAKE_ABSOLUTE_URL + Api.COMMON_REQUSET + CelebrationReplyControllerAPI.COMMON_URL + "/" + CELEBRATION_REPLY_NO)
					.locale(locale)
					.session(TestSession.getAdminSession())).andDo(print()).andExpect(status().isBadRequest()).andReturn();
			
			String resultBody = mvcResult.getResponse()
					.getContentAsString(Charset.forName(TestConstant.DEFAULT_CHARSET));
			ErrorResponse result = om.readValue(resultBody, ErrorResponse.class);
			
			assertEquals(new ErrorResponse(AzusatoException.I0006, messageSource.getMessage(AzusatoException.I0006, null, locale)),
					result);
		}

		@ParameterizedTest
		@MethodSource("com.my.azusato.common.TestSource#locales")
		public void givenNodata_result400(Locale locale) throws Exception {
			MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders
					.delete(TestConstant.MAKE_ABSOLUTE_URL + Api.COMMON_REQUSET + CelebrationReplyControllerAPI.COMMON_URL + "/" + "1000")
					.locale(locale)
					.session(TestSession.getAdminSession())).andDo(print()).andExpect(status().isBadRequest()).andReturn();

			String resultBody = mvcResult.getResponse()
					.getContentAsString(Charset.forName(TestConstant.DEFAULT_CHARSET));
			ErrorResponse result = om.readValue(resultBody, ErrorResponse.class);
			String tableName = messageSource.getMessage(CelebrationReplyEntity.TABLE_NAME_KEY, null, locale);
			
			assertEquals(new ErrorResponse(AzusatoException.I0005, messageSource.getMessage(AzusatoException.I0005, new String[] {tableName}, locale)),
					result);
		}
		
		@ParameterizedTest
		@MethodSource("com.my.azusato.common.TestSource#locales")
		public void givenNoSession_result401(Locale locale) throws Exception {
			MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders
					.delete(TestConstant.MAKE_ABSOLUTE_URL + Api.COMMON_REQUSET + CelebrationReplyControllerAPI.COMMON_URL + "/" + CELEBRATION_REPLY_NO)
					.locale(locale))
					.andDo(print()).andExpect(status().isUnauthorized()).andReturn();

			String resultBody = mvcResult.getResponse()
					.getContentAsString(Charset.forName(TestConstant.DEFAULT_CHARSET));
			ErrorResponse result = om.readValue(resultBody, ErrorResponse.class);

			assertEquals(new ErrorResponse(AzusatoException.I0001, messageSource.getMessage(AzusatoException.I0001, null, locale)),
					result);
		}
		
		@ParameterizedTest
		@MethodSource("com.my.azusato.common.TestSource#locales")
		public void givenPathValueTypeError_result400(Locale locale)
				throws Exception {
			MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders
					.delete(TestConstant.MAKE_ABSOLUTE_URL + Api.COMMON_REQUSET + CelebrationReplyControllerAPI.COMMON_URL + "/" + "string")
					.session(TestSession.getAdminSession())
					.locale(locale))
			.andDo(print()).andExpect(status().is(400)).andReturn();


			String resultBody = mvcResult.getResponse()
					.getContentAsString(Charset.forName(TestConstant.DEFAULT_CHARSET));
			ErrorResponse result = om.readValue(resultBody, ErrorResponse.class);
			
			String message = messageSource.getMessage(AzusatoException.I0008, null, locale);

			assertEquals(new ErrorResponse(AzusatoException.I0008, message), result);
		}
	}
	
}
