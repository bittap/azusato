package com.my.azusato.integration.view.wedding;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import com.my.azusato.common.TestConstant;
import com.my.azusato.common.TestLogin;
import com.my.azusato.integration.AbstractIntegrationForTest;
import com.my.azusato.view.controller.common.UrlConstant;

public class InvitationControllerTest extends AbstractIntegrationForTest {

  @Nested
  public class invitation {

    private static final String URL = "/wedding";

    @ParameterizedTest
    @ValueSource(strings = {UrlConstant.COMMON_ADMIN_CONTROLLER_REQUSET + URL,
        "/" + TestConstant.LOCALE_JA_STR + UrlConstant.COMMON_ADMIN_CONTROLLER_REQUSET + URL,
        "/" + TestConstant.LOCALE_KO_STR + UrlConstant.COMMON_ADMIN_CONTROLLER_REQUSET + URL})
    public void givenUrl_resultNot404(String url) throws Exception {
      mockMvc.perform(MockMvcRequestBuilders.get(url).with(user(TestLogin.adminLoginUser())))
          .andExpect(status().is(200)).andReturn();
    }
  }
}
