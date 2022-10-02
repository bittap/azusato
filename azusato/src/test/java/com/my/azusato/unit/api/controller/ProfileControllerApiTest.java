package com.my.azusato.unit.api.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import java.nio.charset.Charset;
import java.util.Locale;
import java.util.stream.Stream;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.my.azusato.anonotation.UnitController;
import com.my.azusato.api.controller.ProfileControllerAPI;
import com.my.azusato.api.service.ProfileServiceAPI;
import com.my.azusato.common.TestConstant;
import com.my.azusato.common.TestLogin;
import com.my.azusato.common.TestStream;
import com.my.azusato.exception.AzusatoException;
import com.my.azusato.exception.ErrorResponse;
import com.my.azusato.view.controller.common.UrlConstant.Api;

@UnitController
@Import(value = ProfileControllerAPI.class)
class ProfileControllerApiTest {

  @Autowired
  MockMvc mockMvc;

  ObjectMapper om = new ObjectMapper();

  @MockBean
  ProfileServiceAPI mockProfileService;

  @Nested
  @Disabled("結合テストで対応")
  class getDefaultRandomProfile {

  }

  @Nested
  class uploadImage {

    @Nested
    @DisplayName("正常系")
    @Disabled("結合テストで対応")
    class normal {

    }

    @Nested
    @DisplayName("準正常系")
    class subnormal {

      @ParameterizedTest
      @MethodSource("com.my.azusato.unit.api.controller.ProfileControllerApiTest#uploadImage_givenNoPermitExtension_resultThrow400")
      @DisplayName("容認していない拡張子ファイルアップロード_結果400をスローする")
      void givenNoPermitExtensionFileUplod_resultThrow400(Locale locale, String message)
          throws Exception {
        final String NO_PERMIT_EXTENTION_FILENAME = "test.video";
        MockMultipartFile multipartFile = new MockMultipartFile("profileImage",
            NO_PERMIT_EXTENTION_FILENAME, null, TestStream.getTestImageBytes());

        MvcResult mvcResult = mockMvc
            .perform(multipart(TestConstant.MAKE_ABSOLUTE_URL + Api.COMMON_REQUSET
                + ProfileControllerAPI.UPLOAD_IMG_URL) //
                    .file(multipartFile) //
                    .locale(locale) //
                    .with(user(TestLogin.adminLoginUser())) //
                    .with(csrf())) //
            .andDo(print()) //
            .andExpect(status().isBadRequest()) //
            .andReturn(); //

        String resultBody = mvcResult.getResponse()
            .getContentAsString(Charset.forName(TestConstant.DEFAULT_CHARSET));
        ErrorResponse result = om.readValue(resultBody, ErrorResponse.class);

        assertEquals(new ErrorResponse(AzusatoException.I0004, message), result);
      }

      @ParameterizedTest
      @MethodSource("com.my.azusato.unit.api.controller.ProfileControllerApiTest#uploadImage_givenParameterNull_resultThrow400")
      @DisplayName("パラメータがnull_結果400をスローする")
      void givenParameterNull_resultThrow400(Locale locale, String message) throws Exception {
        MvcResult mvcResult = mockMvc
            .perform(multipart(TestConstant.MAKE_ABSOLUTE_URL + Api.COMMON_REQUSET
                + ProfileControllerAPI.UPLOAD_IMG_URL) //
                    .locale(locale) //
                    .with(user(TestLogin.adminLoginUser())) //
                    .with(csrf())) //
            .andDo(print()) //
            .andExpect(status().isBadRequest()) //
            .andReturn(); //

        String resultBody = mvcResult.getResponse()
            .getContentAsString(Charset.forName(TestConstant.DEFAULT_CHARSET));
        ErrorResponse result = om.readValue(resultBody, ErrorResponse.class);

        assertEquals(new ErrorResponse(AzusatoException.I0004, message), result);
      }
    }

  }

  static Stream<Arguments> uploadImage_givenParameterNull_resultThrow400() {
    return Stream.of(Arguments.of(Locale.JAPANESE, "プロフィールイメージは必修項目です。"),
        Arguments.of(Locale.KOREAN, "프로필이미지을 입력해주세요."));

  }

  static Stream<Arguments> uploadImage_givenNoPermitExtension_resultThrow400() {
    return Stream.of(
        Arguments.of(Locale.JAPANESE,
            "ファイルの拡張子videoは使用不可能です。\nプロフィールイメージに使用可能なファイルの拡張子はpng,jpeg,jpgのみです。"),
        Arguments.of(Locale.KOREAN,
            "파일확장자 video는 사용할 수 없습니다.\n프로필이미지에 사용가능한 파일의 확장자는 png,jpeg,jpg입니다."));

  }
}
