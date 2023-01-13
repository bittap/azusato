package com.my.azusato.integration.interceptor;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import javax.servlet.http.Cookie;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.util.NestedServletException;
import com.my.azusato.common.TestConstant;
import com.my.azusato.entity.UserEntity;
import com.my.azusato.integration.AbstractIntegration;
import com.my.azusato.login.LoginUser;
import com.my.azusato.view.controller.common.CookieConstant;

public class LoginInterceptorTest extends AbstractIntegration {

  private final String NOT_EXIST_URL = "test";

  @Nested
  class PreHandle {

    @Test
    public void givenSession_resultSessionRefrashed() throws Exception {
      int shortIntervalSessionTime = 1;
      MockHttpSession session = new MockHttpSession();
      session.setMaxInactiveInterval(shortIntervalSessionTime);

      UserEntity targetUser = getAdminUser();

      MvcResult mvcResult = mockMvc
          .perform(MockMvcRequestBuilders.get(TestConstant.MAKE_ABSOLUTE_URL + NOT_EXIST_URL)
              .with(user(new LoginUser(targetUser))))
          .andDo(print()).andExpect(authenticated()).andReturn();

      int sessionMaxInterval = mvcResult.getRequest().getSession().getMaxInactiveInterval();

      org.hamcrest.MatcherAssert.assertThat("check if session is refreshed", sessionMaxInterval,
          Matchers.greaterThan(shortIntervalSessionTime));

    }

    @Test
    public void givenNonmemberCookie_resultSessionExist() throws Exception {
      UserEntity targetUser = getNonMember();
      LoginUser loginUser = new LoginUser(targetUser);

      Cookie cookie = new Cookie(CookieConstant.NON_MEMBER_KEY, loginUser.getUsername());

      mockMvc
          .perform(MockMvcRequestBuilders.get(TestConstant.MAKE_ABSOLUTE_URL + NOT_EXIST_URL)
              .cookie(cookie))
          .andDo(print()).andExpect(authenticated().withUsername(loginUser.getUsername())
              .withAuthorities(loginUser.getAuthorities()));
    }

    @Test
    public void givenNonmemberCookieAndNotExistUserEntity_whenSelectUserEntity_resultThrowNullPointerException()
        throws Exception {
      Cookie cookie = new Cookie(CookieConstant.NON_MEMBER_KEY, "notExistUser");

      Assertions.assertThrows(NestedServletException.class, () -> {
        mockMvc.perform(MockMvcRequestBuilders.get(TestConstant.MAKE_ABSOLUTE_URL + NOT_EXIST_URL)
            .cookie(cookie)).andDo(print());
      });
    }

    @Test
    public void givenEtcCookie_resultNotExistSession() throws Exception {
      Cookie cookie = new Cookie("etc", "etc");
      mockMvc
          .perform(MockMvcRequestBuilders.get(TestConstant.MAKE_ABSOLUTE_URL + NOT_EXIST_URL)
              .cookie(cookie))
          .andDo(print()).andExpect(request().sessionAttributeDoesNotExist(
              HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY));

    }

    @Test
    public void givenNoCookie_resultNotExistSession() throws Exception {
      mockMvc.perform(MockMvcRequestBuilders.get(TestConstant.MAKE_ABSOLUTE_URL + NOT_EXIST_URL))
          .andDo(print()).andExpect(request().sessionAttributeDoesNotExist(
              HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY));

    }
  }
}
