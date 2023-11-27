package com.my.azusato.integration.api.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
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
import com.my.azusato.integration.AbstractIntegrationForTest;
import com.my.azusato.login.Grant;
import com.my.azusato.property.CookieProperty;

public class LoginControllerAPITest extends AbstractIntegrationForTest {

  @Autowired
  private CookieProperty cookieProperty;

  @Nested
  class Login {

    @ParameterizedTest
    @MethodSource("com.my.azusato.integration.api.controller.LoginControllerAPITest#thenRealtiveUser_resultOk")
    public void thenRealtiveUser_resultOk(Long no) throws Exception {
      UserEntity targetUser = userRepo.findById(no).get();

      List<GrantedAuthority> expectedRoles = AuthorityUtils
          .createAuthorityList(Grant.ROLE_PRIFIX + targetUser.getUserType().toString());

      mockMvc
          .perform(MockMvcRequestBuilders.post(SecurityConfig.API_LOGIN_URL)
              .param(SecurityConfig.USERNAME_PARAMETER, targetUser.getId())
              .param(SecurityConfig.PASSWORD_PARAMETER, Entity.createdVarChars[1]).with(csrf()))
          .andDo(print())
          .andExpect(
              authenticated().withUsername(targetUser.getId()).withAuthorities(expectedRoles))
          .andExpect(status().isOk()).andReturn();
    }

    @Test
    public void givenSavedIdTrue_thenResultsavedCookie_and_givenSavedIdFalse_thenResultdeleteCookie()
        throws Exception {
      UserEntity targetUser = userRepo.findById(1L).get();
      String id = targetUser.getId();
      // ユーザID保存 trueの場合
      mockMvc
          .perform(MockMvcRequestBuilders.post(SecurityConfig.API_LOGIN_URL)
              .param(SecurityConfig.USERNAME_PARAMETER, id)
              .param(SecurityConfig.PASSWORD_PARAMETER, Entity.createdVarChars[1])
              .param(cookieProperty.getLoginSaveIdName(), "true").with(csrf()))
          .andDo(print()).andExpect(status().isOk())
          .andExpect(cookie().exists(cookieProperty.getLoginSaveIdName()))
          .andExpect(cookie().maxAge(cookieProperty.getLoginSaveIdName(),
              cookieProperty.getCookieMaxTime()))
          .andExpect(cookie().value(cookieProperty.getLoginSaveIdName(), id))
          .andExpect(cookie().path(cookieProperty.getLoginSaveIdName(), cookieProperty.getPath()))
          .andReturn();

      // ユーザID保存 falseの場合
      mockMvc
          .perform(MockMvcRequestBuilders.post(SecurityConfig.API_LOGIN_URL)
              .param(SecurityConfig.USERNAME_PARAMETER, id)
              .param(SecurityConfig.PASSWORD_PARAMETER, Entity.createdVarChars[1])
              .param(cookieProperty.getLoginSaveIdName(), "false").with(csrf()))
          .andDo(print()).andExpect(status().isOk())
          .andExpect(cookie().exists(cookieProperty.getLoginSaveIdName()))
          .andExpect(cookie().maxAge(cookieProperty.getLoginSaveIdName(), 0))
          .andExpect(cookie().path(cookieProperty.getLoginSaveIdName(), cookieProperty.getPath()))
          .andReturn();
    }



    @ParameterizedTest
    @MethodSource("com.my.azusato.common.TestSource#locales")
    public void thenNotMatchPassword_result400(Locale locale) throws Exception {
      UserEntity targetUser = userRepo.findById(1L).get();

      MvcResult mvcResult =
          mockMvc.perform(MockMvcRequestBuilders.post(SecurityConfig.API_LOGIN_URL)
              .param(SecurityConfig.USERNAME_PARAMETER, targetUser.getId())
              .param(SecurityConfig.PASSWORD_PARAMETER, "notMatchedPassword").with(csrf())
              .locale(locale)).andDo(print()).andExpect(status().isBadRequest()).andReturn();

      String resultBody =
          mvcResult.getResponse().getContentAsString(Charset.forName(TestConstant.DEFAULT_CHARSET));
      ErrorResponse result = om.readValue(resultBody, ErrorResponse.class);

      assertEquals(new ErrorResponse(AzusatoException.I0010,
          messageSource.getMessage(AzusatoException.I0010, null, locale)), result);
    }

    @ParameterizedTest
    @MethodSource("com.my.azusato.common.TestSource#locales")
    public void thenNotFoundUser_result400(Locale locale) throws Exception {
      MvcResult mvcResult =
          mockMvc.perform(MockMvcRequestBuilders.post(SecurityConfig.API_LOGIN_URL)
              .param(SecurityConfig.USERNAME_PARAMETER, Entity.createdVarChars[0])
              .param(SecurityConfig.PASSWORD_PARAMETER, Entity.createdVarChars[1]).with(csrf())
              .locale(locale)).andDo(print()).andExpect(status().isBadRequest()).andReturn();

      String resultBody =
          mvcResult.getResponse().getContentAsString(Charset.forName(TestConstant.DEFAULT_CHARSET));
      ErrorResponse result = om.readValue(resultBody, ErrorResponse.class);

      assertEquals(new ErrorResponse(AzusatoException.I0009,
          messageSource.getMessage(AzusatoException.I0009, null, locale)), result);
    }

  }

  @Nested
  class Logout {

    @Test
    public void givenLogInAndLogout_thenResultOk() throws Exception {
      UserEntity targetUser = userRepo.findById(1L).get();

      // login
      mockMvc
          .perform(MockMvcRequestBuilders.post(SecurityConfig.API_LOGIN_URL)
              .param(SecurityConfig.USERNAME_PARAMETER, targetUser.getId())
              .param(SecurityConfig.PASSWORD_PARAMETER, Entity.createdVarChars[1]).with(csrf()))
          .andDo(print()).andExpect(status().isOk()).andReturn();

      // logout
      mockMvc.perform(MockMvcRequestBuilders.post(SecurityConfig.API_LOGOUT_URL).with(csrf()))
          .andDo(print()).andExpect(status().isOk()).andReturn();
    }
  }

  public static Stream<Long> thenRealtiveUser_resultOk() {
    return Stream.of(1L, 2L, 3L, 4L);
  }
}
