package com.my.azusato.unit.api.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import java.nio.charset.Charset;
import java.util.Locale;
import java.util.stream.Stream;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.servlet.HandlerInterceptor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.my.azusato.api.controller.CelebrationReplyControllerAPI;
import com.my.azusato.api.controller.request.AddCelebrationReplyAPIReqeust;
import com.my.azusato.api.service.CelebrationReplyServiceAPI;
import com.my.azusato.common.TestConstant;
import com.my.azusato.common.TestConstant.Entity;
import com.my.azusato.common.TestLogin;
import com.my.azusato.config.WebMvcConfig;
import com.my.azusato.exception.AzusatoException;
import com.my.azusato.exception.ErrorResponse;
import com.my.azusato.view.controller.common.HttpConstant;
import com.my.azusato.view.controller.common.UrlConstant.Api;

@Import(value = InMemoryUserDetailsManager.class)
@WebMvcTest(controllers = CelebrationReplyControllerAPI.class,
    excludeFilters = {@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
        classes = {WebMvcConfig.class, HandlerInterceptor.class})}) // Interceptor除外
class CelebrationReplyControllerApiTest {

  @Autowired
  MockMvc mockMvc;

  ObjectMapper om = new ObjectMapper();

  @MockBean
  CelebrationReplyServiceAPI mockCeleReplyService;

  @Nested
  class AddReplyCelebration {

    final String CELEBRATION_REPLY_NO = "1";

    @Nested
    @DisplayName("正常系")
    @Disabled("結合テストで対応")
    class normal {

    }

    @Nested
    @DisplayName("準正常系")
    class subnormal {

      @ParameterizedTest
      @MethodSource("com.my.azusato.common.TestSource#I0001_Message")
      @DisplayName("セッションがない_結果401をスローする")
      public void givenNoSession_resultThrow401(Locale locale, String expectMessage)
          throws Exception {
        MvcResult mvcResult = mockMvc
            .perform(MockMvcRequestBuilders
                .post(TestConstant.MAKE_ABSOLUTE_URL + Api.COMMON_REQUSET
                    + CelebrationReplyControllerAPI.COMMON_URL + "/" + CELEBRATION_REPLY_NO)
                .with(csrf()) ///
                .content(getRequestBody()) //
                .contentType(HttpConstant.DEFAULT_CONTENT_TYPE_STRING) //
                .locale(locale)) //
            .andDo(print()) //
            .andExpect(status().is(401)) //
            .andReturn(); //

        String resultBody = mvcResult.getResponse()
            .getContentAsString(Charset.forName(TestConstant.DEFAULT_CHARSET));
        ErrorResponse result = om.readValue(resultBody, ErrorResponse.class);

        assertEquals(new ErrorResponse(AzusatoException.I0001, expectMessage), result);
      }

      @ParameterizedTest
      @MethodSource("com.my.azusato.common.TestSource#I0008_Message")
      @DisplayName("パスの値が異常の場合_結果400をスローする")
      public void givenPathValueTypeError_resultThrow400(Locale locale, String expectMessage)
          throws Exception {
        MvcResult mvcResult = mockMvc
            .perform(MockMvcRequestBuilders
                .post(TestConstant.MAKE_ABSOLUTE_URL + Api.COMMON_REQUSET
                    + CelebrationReplyControllerAPI.COMMON_URL + "/" + "string")
                .with(csrf()) //
                .content(getRequestBody()) //
                .contentType(HttpConstant.DEFAULT_CONTENT_TYPE_STRING)
                .with(user(TestLogin.adminLoginUser())) //
                .locale(locale)) //
            .andDo(print()) //
            .andExpect(status().is(400)) //
            .andReturn();


        String resultBody = mvcResult.getResponse()
            .getContentAsString(Charset.forName(TestConstant.DEFAULT_CHARSET));
        ErrorResponse result = om.readValue(resultBody, ErrorResponse.class);

        assertEquals(new ErrorResponse(AzusatoException.I0008, expectMessage), result);
      }

      @ParameterizedTest
      @MethodSource("com.my.azusato.unit.api.controller.CelebrationReplyControllerApiTest#AddReplyCelebration_givenParameterError_result400")
      public void givenParameterError_resultThrow400(Locale locale,
          AddCelebrationReplyAPIReqeust req, String expectedMessage) throws Exception {
        String requestBody = om.writeValueAsString(req);

        MvcResult mvcResult = mockMvc
            .perform(MockMvcRequestBuilders
                .post(TestConstant.MAKE_ABSOLUTE_URL + Api.COMMON_REQUSET
                    + CelebrationReplyControllerAPI.COMMON_URL + "/" + CELEBRATION_REPLY_NO)
                .with(csrf()) //
                .content(requestBody) //
                .contentType(HttpConstant.DEFAULT_CONTENT_TYPE_STRING) //
                .locale(locale)) //
            .andDo(print()).andExpect(status().is(400)).andReturn();


        String resultBody = mvcResult.getResponse()
            .getContentAsString(Charset.forName(TestConstant.DEFAULT_CHARSET));
        ErrorResponse result = om.readValue(resultBody, ErrorResponse.class);

        assertEquals(new ErrorResponse(AzusatoException.I0004, expectedMessage), result);
      }
    }

    private String getRequestBody() throws Exception {
      AddCelebrationReplyAPIReqeust req = AddCelebrationReplyAPIReqeust.builder()
          .name(Entity.updatedVarChars[0]).content(Entity.createdVarChars[0]).build();

      return om.writeValueAsString(req);
    }
  }

  @Nested
  class DeleteCeleReplybration {

    final String CELEBRATION_REPLY_NO = "1";

    @ParameterizedTest
    @MethodSource("com.my.azusato.common.TestSource#I0001_Message")
    @DisplayName("セッションがない_結果401をスローする")
    public void givenNoSession_result401(Locale locale, String expectMessage) throws Exception {
      MvcResult mvcResult = mockMvc
          .perform(MockMvcRequestBuilders
              .delete(TestConstant.MAKE_ABSOLUTE_URL + Api.COMMON_REQUSET
                  + CelebrationReplyControllerAPI.COMMON_URL + "/" + CELEBRATION_REPLY_NO)
              .with(csrf()).locale(locale))
          .andDo(print()).andExpect(status().isUnauthorized()).andReturn();

      String resultBody =
          mvcResult.getResponse().getContentAsString(Charset.forName(TestConstant.DEFAULT_CHARSET));
      ErrorResponse result = om.readValue(resultBody, ErrorResponse.class);

      assertEquals(new ErrorResponse(AzusatoException.I0001, expectMessage), result);
    }

    @ParameterizedTest
    @MethodSource("com.my.azusato.common.TestSource#I0008_Message")
    @DisplayName("パスの値が異常の場合_結果400をスローする")
    public void givenPathValueTypeError_result400(Locale locale, String expectMessage)
        throws Exception {
      MvcResult mvcResult = mockMvc
          .perform(MockMvcRequestBuilders
              .delete(TestConstant.MAKE_ABSOLUTE_URL + Api.COMMON_REQUSET
                  + CelebrationReplyControllerAPI.COMMON_URL + "/" + "string")
              .with(csrf()) //
              .locale(locale)) //
          .andDo(print()).andExpect(status().is(400)).andReturn();


      String resultBody =
          mvcResult.getResponse().getContentAsString(Charset.forName(TestConstant.DEFAULT_CHARSET));
      ErrorResponse result = om.readValue(resultBody, ErrorResponse.class);

      assertEquals(new ErrorResponse(AzusatoException.I0008, expectMessage), result);
    }


  }

  static Stream<Arguments> AddReplyCelebration_givenParameterError_result400() {

    return Stream.of(
        Arguments.of(TestConstant.LOCALE_JA,
            AddCelebrationReplyAPIReqeust.builder().name(null).content(Entity.createdVarChars[1])
                .build(),
            "名前は必修項目です。"),
        Arguments.of(TestConstant.LOCALE_JA,
            AddCelebrationReplyAPIReqeust.builder().name(RandomStringUtils.randomAlphabetic(11))
                .content(Entity.createdVarChars[1]).build(),
            "名前は最大10桁数まで入力可能です。"),
        Arguments.of(TestConstant.LOCALE_JA,
            AddCelebrationReplyAPIReqeust.builder().name(Entity.createdVarChars[1])
                .content(RandomStringUtils.randomAlphabetic(501)).build(),
            "内容は最大500桁数まで入力可能です。"),
        Arguments.of(TestConstant.LOCALE_JA,
            AddCelebrationReplyAPIReqeust.builder().name(Entity.createdVarChars[0]).content(null)
                .build(),
            "内容は必修項目です。"),
        Arguments.of(TestConstant.LOCALE_KO,
            AddCelebrationReplyAPIReqeust.builder().name(null).content(Entity.createdVarChars[1])
                .build(),
            "이름을 입력해주세요."),
        Arguments.of(TestConstant.LOCALE_KO,
            AddCelebrationReplyAPIReqeust.builder().name(Entity.createdVarChars[0]).content(null)
                .build(),
            "내용을 입력해주세요."),
        Arguments.of(TestConstant.LOCALE_KO,
            AddCelebrationReplyAPIReqeust.builder().name(RandomStringUtils.randomAlphabetic(11))
                .content(Entity.createdVarChars[1]).build(),
            "글자 수 10을 초과해서 이름을 입력하는 것은 불가능합니다."),
        Arguments.of(TestConstant.LOCALE_KO,
            AddCelebrationReplyAPIReqeust.builder().name(Entity.createdVarChars[1])
                .content(RandomStringUtils.randomAlphabetic(501)).build(),
            "글자 수 500을 초과해서 내용을 입력하는 것은 불가능합니다."),
        Arguments.of(TestConstant.LOCALE_JA, AddCelebrationReplyAPIReqeust.builder().build(),
            "内容は必修項目です。\n名前は必修項目です。"));

  }
}
