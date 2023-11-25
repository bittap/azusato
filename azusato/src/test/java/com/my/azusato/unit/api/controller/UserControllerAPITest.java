package com.my.azusato.unit.api.controller;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import java.nio.charset.Charset;
import java.util.Locale;
import java.util.stream.Stream;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.my.azusato.anonotation.UnitControllerForTest;
import com.my.azusato.api.controller.UserControllerAPI;
import com.my.azusato.api.controller.request.AddNonMemberUserAPIRequest;
import com.my.azusato.api.service.UserServiceAPI;
import com.my.azusato.api.service.response.GetSessionUserServiceAPIResponse;
import com.my.azusato.common.TestConstant;
import com.my.azusato.common.TestConstant.Entity;
import com.my.azusato.common.TestCookie;
import com.my.azusato.common.TestLogin;
import com.my.azusato.exception.AzusatoException;
import com.my.azusato.exception.ErrorResponse;
import com.my.azusato.login.LoginUser;
import com.my.azusato.view.controller.common.CookieConstant;
import com.my.azusato.view.controller.common.HttpConstant;
import com.my.azusato.view.controller.common.UrlConstant.Api;

@UnitControllerForTest
@Import(value = UserControllerAPI.class)
public class UserControllerAPITest {

  @Autowired
  MockMvc mockMvc;

  ObjectMapper om = new ObjectMapper();

  @MockBean
  UserServiceAPI mockUserServiceAPI;

  @Nested
  class getLoginUser {

    @Nested
    @DisplayName("正常系")
    class normal {

      @Test
      @DisplayName("ログインユーザが存在_結果ユーザ情報を返す")
      void givenLoginUser_resultReturnUserInfo() throws Exception {
        // 準備
        LoginUser loginUser = TestLogin.adminLoginUser();
        GetSessionUserServiceAPIResponse mockResult = GetSessionUserServiceAPIResponse.builder() //
            .name("名前") //
            .profileImagePath("profileImagePath") //
            .id(loginUser.getUsername()) //
            .build(); //
        when(mockUserServiceAPI.getSessionUser(Mockito.eq(loginUser.getUSER_NO()), Mockito.any()))
            .thenReturn(mockResult);

        MvcResult mvcResult = mockMvc
            .perform(MockMvcRequestBuilders.get(
                TestConstant.MAKE_ABSOLUTE_URL + Api.COMMON_REQUSET + UserControllerAPI.COMMON_URL)
                .with(user(TestLogin.adminLoginUser()))) //
            .andDo(print()) //
            .andExpect(status().isOk()) //
            .andReturn();

        String resultStrBody = mvcResult.getResponse()
            .getContentAsString(Charset.forName(TestConstant.DEFAULT_CHARSET));
        GetSessionUserServiceAPIResponse result =
            om.readValue(resultStrBody, GetSessionUserServiceAPIResponse.class);

        GetSessionUserServiceAPIResponse expect = GetSessionUserServiceAPIResponse.builder()
            .id(mockResult.getId()).name(mockResult.getName())
            .profileImagePath(mockResult.getProfileImagePath()).build();

        Assertions.assertEquals(expect, result);
      }
    }

    @Nested
    @DisplayName("準正常系")
    class subnormal {

      @ParameterizedTest
      @MethodSource("com.my.azusato.common.TestSource#I0002_Message")
      @DisplayName("ログインしていない状態_エラーをスローする")
      void givenNotLogin_resultThrow400(Locale locale, String expectMessage) throws Exception {
        MvcResult mvcResult = mockMvc
            .perform(MockMvcRequestBuilders
                .get(TestConstant.MAKE_ABSOLUTE_URL + Api.COMMON_REQUSET
                    + UserControllerAPI.COMMON_URL) //
                .locale(locale)) //
            .andDo(print()) //
            .andExpect(status().isBadRequest()) //
            .andReturn();

        String resultStrBody = mvcResult.getResponse()
            .getContentAsString(Charset.forName(TestConstant.DEFAULT_CHARSET));
        ErrorResponse result = om.readValue(resultStrBody, ErrorResponse.class);

        ErrorResponse expect = new ErrorResponse(AzusatoException.I0002, expectMessage);

        Assertions.assertEquals(expect, result);
      }

    }
  }

  @Nested
  class addNonMember {

    @Nested
    @DisplayName("正常系")
    class normal {

      @Test
      @DisplayName("非会員ユーザのクッキーが存在しない_結果非会員クッキーを生成する")
      public void givenNoNonMemberCookie_resultCookieIsCreated() throws Exception {
        String requestBody = om.writeValueAsString(
            AddNonMemberUserAPIRequest.builder().name(Entity.createdVarChars[0]).build());

        mockMvc
            .perform(MockMvcRequestBuilders
                .post(TestConstant.MAKE_ABSOLUTE_URL + Api.COMMON_REQUSET
                    + UserControllerAPI.ADD_NONMEMBER_URL)
                .with(csrf()) //
                .content(requestBody) //
                .contentType(HttpConstant.DEFAULT_CONTENT_TYPE_STRING)) //
            .andDo(print()) //
            .andExpect(status().isCreated()) //
            .andExpect(cookie().value(CookieConstant.NON_MEMBER_KEY, notNullValue()));
      }

    }

    @Nested
    @DisplayName("準正常系")
    class subnormal {

      @ParameterizedTest
      @MethodSource("com.my.azusato.unit.api.controller.UserControllerAPITest#addNonMember_givenInvaildParameter_result400")
      @DisplayName("無効なパラメータ_結果例外をスローする")
      void givenInvaildParameter_resultThrow400(Locale locale, AddNonMemberUserAPIRequest req,
          String expectedMessage) throws Exception {
        String requestBody = om.writeValueAsString(req);
        MvcResult mvcResult = mockMvc
            .perform(MockMvcRequestBuilders
                .post(TestConstant.MAKE_ABSOLUTE_URL + Api.COMMON_REQUSET
                    + UserControllerAPI.ADD_NONMEMBER_URL) //
                .content(requestBody) //
                .contentType(HttpConstant.DEFAULT_CONTENT_TYPE_STRING) //
                .with(csrf())//
                .locale(locale)) //
            .andDo(print()) //
            .andExpect(status().is(400)) //
            .andReturn(); //

        String resultBody = mvcResult.getResponse()
            .getContentAsString(Charset.forName(TestConstant.DEFAULT_CHARSET));
        ErrorResponse result = om.readValue(resultBody, ErrorResponse.class);

        assertEquals(new ErrorResponse(AzusatoException.I0004, expectedMessage), result);
      }

      @ParameterizedTest
      @MethodSource("com.my.azusato.common.TestSource#I0003_Message")
      @DisplayName("既にクッキーが存在する_結果例外をスローする")
      void givenAlreadyCookie_resultThrow400(Locale locale, String expectedMessage)
          throws Exception {
        AddNonMemberUserAPIRequest req =
            AddNonMemberUserAPIRequest.builder().name(Entity.createdVarChars[0]).build();
        String requestBody = om.writeValueAsString(req);

        MvcResult mvcResult = mockMvc
            .perform(MockMvcRequestBuilders
                .post(TestConstant.MAKE_ABSOLUTE_URL + Api.COMMON_REQUSET
                    + UserControllerAPI.ADD_NONMEMBER_URL)
                .with(csrf()) //
                .locale(locale) //
                .contentType(HttpConstant.DEFAULT_CONTENT_TYPE_STRING) //
                .content(requestBody).cookie(TestCookie.getNonmemberCookie()) //
                .contentType(HttpConstant.DEFAULT_CONTENT_TYPE_STRING)) //
            .andDo(print()) //
            .andExpect(status().isBadRequest()) //
            .andReturn(); //

        String resultBody = mvcResult.getResponse()
            .getContentAsString(Charset.forName(TestConstant.DEFAULT_CHARSET));
        ErrorResponse result = om.readValue(resultBody, ErrorResponse.class);

        assertEquals(new ErrorResponse(AzusatoException.I0003, expectedMessage), result);
      }
    }

  }

  static Stream<Arguments> addNonMember_givenInvaildParameter_result400() {
    return Stream.of(
        Arguments.of(Locale.JAPANESE,
            AddNonMemberUserAPIRequest.builder().name(RandomStringUtils.randomAlphabetic(11))
                .build(),
            "名前は最大10桁数まで入力可能です。"),
        Arguments.of(Locale.JAPANESE, AddNonMemberUserAPIRequest.builder().build(), "名前は必修項目です。"),
        Arguments.of(Locale.KOREAN, AddNonMemberUserAPIRequest.builder().build(), "이름을 입력해주세요."),
        Arguments.of(
            Locale.KOREAN, AddNonMemberUserAPIRequest.builder()
                .name(RandomStringUtils.randomAlphabetic(11)).build(),
            "글자 수 10을 초과해서 이름을 입력하는 것은 불가능합니다."));
  }
}
