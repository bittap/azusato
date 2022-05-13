package com.my.azusato.integration.api.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.stream.Stream;

import javax.servlet.http.Cookie;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.my.azusato.api.controller.request.AddCelebrationAPIReqeust;
import com.my.azusato.common.TestConstant;
import com.my.azusato.common.TestConstant.Entity;
import com.my.azusato.common.TestCookie;
import com.my.azusato.common.TestSession;
import com.my.azusato.exception.AzusatoException;
import com.my.azusato.exception.ErrorResponse;
import com.my.azusato.integration.AbstractIntegration;
import com.my.azusato.property.ProfileProperty;
import com.my.azusato.view.controller.common.HttpConstant;
import com.my.azusato.view.controller.common.UrlConstant.Api;

public class CelebrationContollerAPITest extends AbstractIntegration {

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
					.post(TestConstant.MAKE_ABSOLUTE_URL + Api.COMMON_REQUSET + Api.CELEBRATION_CONTROLLER_REQUSET
							+ "add")
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
							.post(TestConstant.MAKE_ABSOLUTE_URL + Api.COMMON_REQUSET
									+ Api.CELEBRATION_CONTROLLER_REQUSET + "add")
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
							.post(TestConstant.MAKE_ABSOLUTE_URL + Api.COMMON_REQUSET
									+ Api.CELEBRATION_CONTROLLER_REQUSET + "add")
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

	@SuppressWarnings("unused")
	private static Stream<Arguments> addCelebration_normal_case() {
		return Stream.of(
				// admin login
				Arguments.of(TestSession.getAdminSession(), TestCookie.getNonmemberCookie(),
						Paths.get(AddCelebration.RESOUCE_PATH, "1", TestConstant.INIT_XML_FILE_NAME),
						Paths.get(AddCelebration.RESOUCE_PATH, "1", TestConstant.EXPECT_XML_FILE_NAME),
						new String[] { "user", "celebration" }),
				// not admin login
				Arguments.of(TestSession.getKakaoSession(), TestCookie.getNonmemberCookie(),
						Paths.get(AddCelebration.RESOUCE_PATH, "2", TestConstant.INIT_XML_FILE_NAME),
						Paths.get(AddCelebration.RESOUCE_PATH, "2", TestConstant.EXPECT_XML_FILE_NAME),
						new String[] { "user", "celebration", "celebration_notice" }),
				// nonmember login
				Arguments.of(new MockHttpSession(), TestCookie.getNonmemberCookie(),
						Paths.get(AddCelebration.RESOUCE_PATH, "3", TestConstant.INIT_XML_FILE_NAME),
						Paths.get(AddCelebration.RESOUCE_PATH, "3", TestConstant.EXPECT_XML_FILE_NAME),
						new String[] { "user", "celebration", "celebration_notice" }));

	}

	@SuppressWarnings("unused")
	private static Stream<Arguments> addCelebration_semi_abnormal_case() {
		final String NORMAL_IMAGE_TYPE = "image/png";
		
		return Stream.of(
				Arguments.of(TestConstant.LOCALE_JA,
						AddCelebrationAPIReqeust.builder()
						.name(Entity.createdVarChars[0]).profileImageBase64(Entity.createdVarChars[2]).profileImageType(NORMAL_IMAGE_TYPE)
						.title(null).content(Entity.createdVarChars[1])
						.build(),
						"タイトルは必修項目です。"),
				Arguments.of(TestConstant.LOCALE_JA,
						AddCelebrationAPIReqeust.builder()
						.name(Entity.createdVarChars[0]).profileImageBase64(Entity.createdVarChars[2]).profileImageType(NORMAL_IMAGE_TYPE)
						.title(RandomStringUtils.randomAlphabetic(51)).content(Entity.createdVarChars[1])
						.build(),
						"タイトルは最大50桁数まで入力可能です。"),
				Arguments.of(TestConstant.LOCALE_JA,
						AddCelebrationAPIReqeust.builder()
						.name(Entity.createdVarChars[0]).profileImageBase64(Entity.createdVarChars[2]).profileImageType(NORMAL_IMAGE_TYPE)
						.title(Entity.createdVarChars[1]).content("")
						.build(),
						"内容は必修項目です。"),
				Arguments.of(TestConstant.LOCALE_JA,
						AddCelebrationAPIReqeust.builder()
						.name(Entity.createdVarChars[0]).profileImageBase64(Entity.createdVarChars[2]).profileImageType(NORMAL_IMAGE_TYPE)
						.title(Entity.createdVarChars[1]).content("<script>alert('asd')</script>")
						.build(),
						"内容は不正な値です。"),
				Arguments.of(TestConstant.LOCALE_JA,
						AddCelebrationAPIReqeust.builder()
						.name(Entity.createdVarChars[0]).profileImageBase64(Entity.createdVarChars[2]).profileImageType("image/gif")
						.title(Entity.createdVarChars[0]).content(Entity.createdVarChars[1])
						.build(),
						"プロフィールイメージはpng、jpegのみ可能です。"),
				Arguments.of(TestConstant.LOCALE_JA,
						AddCelebrationAPIReqeust.builder()
						.name(Entity.createdVarChars[0]).profileImageBase64(Entity.createdVarChars[2]).profileImageType(null)
						.title(Entity.createdVarChars[0]).content(Entity.createdVarChars[1])
						.build(),
						"プロフィールイメージタイプは必修項目です。"),
				Arguments.of(TestConstant.LOCALE_JA,
						AddCelebrationAPIReqeust.builder()
						.name(Entity.createdVarChars[0]).profileImageBase64("").profileImageType(NORMAL_IMAGE_TYPE)
						.title(Entity.createdVarChars[0]).content(Entity.createdVarChars[1])
						.build(),
						"プロフィールイメージ情報は必修項目です。"),
				Arguments.of(TestConstant.LOCALE_JA,
						AddCelebrationAPIReqeust.builder()
						.name("").profileImageBase64(Entity.createdVarChars[2]).profileImageType(NORMAL_IMAGE_TYPE)
						.title(Entity.createdVarChars[0]).content(Entity.createdVarChars[1])
						.build(),
						"名前は必修項目です。"),
				Arguments.of(TestConstant.LOCALE_KO,
						AddCelebrationAPIReqeust.builder()
						.name(Entity.createdVarChars[0]).profileImageBase64(Entity.createdVarChars[2]).profileImageType(NORMAL_IMAGE_TYPE)
						.title(null).content(Entity.createdVarChars[1])
						.build(),
						"제목을 입력해주세요."),
				Arguments.of(TestConstant.LOCALE_KO,
						AddCelebrationAPIReqeust.builder()
						.name(Entity.createdVarChars[0]).profileImageBase64(Entity.createdVarChars[2]).profileImageType(NORMAL_IMAGE_TYPE)
						.title(RandomStringUtils.randomAlphabetic(51)).content(Entity.createdVarChars[1])
						.build(),
						"글자 수 50을 초과해서 제목을 입력하는 것은 불가능합니다."),
				Arguments.of(TestConstant.LOCALE_KO,
						AddCelebrationAPIReqeust.builder()
						.name(Entity.createdVarChars[0]).profileImageBase64(Entity.createdVarChars[2]).profileImageType(NORMAL_IMAGE_TYPE)
						.title(Entity.createdVarChars[1]).content("")
						.build(),
						"내용을 입력해주세요."),
				Arguments.of(TestConstant.LOCALE_KO,
						AddCelebrationAPIReqeust.builder()
						.name(Entity.createdVarChars[0]).profileImageBase64(Entity.createdVarChars[2]).profileImageType(NORMAL_IMAGE_TYPE)
						.title(Entity.createdVarChars[1]).content("<script>alert('asd')</script>")
						.build(),
						"내용을 올바르게 입력해주세요."),
				Arguments.of(TestConstant.LOCALE_KO,
						AddCelebrationAPIReqeust.builder()
						.name(Entity.createdVarChars[0]).profileImageBase64(Entity.createdVarChars[2]).profileImageType("image/gif")
						.title(Entity.createdVarChars[0]).content(Entity.createdVarChars[1])
						.build(),
						"프로필사진은 png, jpeg만 지원됩니다."),
				Arguments.of(TestConstant.LOCALE_KO,
						AddCelebrationAPIReqeust.builder()
						.name(Entity.createdVarChars[0]).profileImageBase64(Entity.createdVarChars[2]).profileImageType(null)
						.title(Entity.createdVarChars[0]).content(Entity.createdVarChars[1])
						.build(),
						"프로필이미지타입을 입력해주세요."),
				Arguments.of(TestConstant.LOCALE_KO,
						AddCelebrationAPIReqeust.builder()
						.name(Entity.createdVarChars[0]).profileImageBase64("").profileImageType(NORMAL_IMAGE_TYPE)
						.title(Entity.createdVarChars[0]).content(Entity.createdVarChars[1])
						.build(),
						"프로필이미지정보을 입력해주세요."),
				Arguments.of(TestConstant.LOCALE_KO,
						AddCelebrationAPIReqeust.builder()
						.name("").profileImageBase64(Entity.createdVarChars[2]).profileImageType(NORMAL_IMAGE_TYPE)
						.title(Entity.createdVarChars[0]).content(Entity.createdVarChars[1])
						.build(),
						"이름을 입력해주세요."),
				Arguments.of(TestConstant.LOCALE_JA,
						AddCelebrationAPIReqeust.builder().build(),
						"タイトルは必修項目です。\nプロフィールイメージタイプは必修項目です。\nプロフィールイメージ情報は必修項目です。\n内容は必修項目です。\n名前は必修項目です。"));

	}
}
