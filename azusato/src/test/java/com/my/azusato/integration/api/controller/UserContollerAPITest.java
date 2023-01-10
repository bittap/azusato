package com.my.azusato.integration.api.controller;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import java.nio.charset.Charset;
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
import com.my.azusato.entity.UserEntity;
import com.my.azusato.integration.AbstractIntegration;
import com.my.azusato.login.LoginUser;
import com.my.azusato.view.controller.common.CookieConstant;
import com.my.azusato.view.controller.common.HttpConstant;
import com.my.azusato.view.controller.common.UrlConstant.Api;

public class UserContollerAPITest extends AbstractIntegration {

  @Nested
  class getLoginUser {

    @Test
    public void givenNormalCase_result200() throws Exception {
      UserEntity adminUser = userRepo.findById(Entity.ADMIN_USER_NOS[0]).get();
      MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders
          .get(TestConstant.MAKE_ABSOLUTE_URL + Api.COMMON_REQUSET + UserControllerAPI.COMMON_URL)
          .with(user(new LoginUser(adminUser))).with(csrf())).andDo(print())
          .andExpect(status().isOk()).andReturn();

      String resultStrBody =
          mvcResult.getResponse().getContentAsString(Charset.forName(TestConstant.DEFAULT_CHARSET));
      GetSessionUserServiceAPIResponse result =
          om.readValue(resultStrBody, GetSessionUserServiceAPIResponse.class);

      GetSessionUserServiceAPIResponse expected =
          GetSessionUserServiceAPIResponse.builder().id(adminUser.getId()).name(adminUser.getName())
              .profileImagePath(adminUser.getProfile().getImagePath()).build();

      Assertions.assertEquals(expected, result);
    }
  }


  @Nested
  class AddNonMember {

    @ParameterizedTest
    @MethodSource("com.my.azusato.integration.api.controller.UserContollerAPITest#givenVaildParameter_resultOk")
    public void givenVaildParameter_resultOk(AddNonMemberUserAPIRequest req) throws Exception {
      String requestBody = om.writeValueAsString(req);

      mockMvc
          .perform(MockMvcRequestBuilders
              .post(TestConstant.MAKE_ABSOLUTE_URL + Api.COMMON_REQUSET
                  + UserControllerAPI.ADD_NONMEMBER_URL)
              .with(csrf()).content(requestBody)
              .contentType(HttpConstant.DEFAULT_CONTENT_TYPE_STRING))
          .andDo(print()).andExpect(status().isCreated())
          .andExpect(cookie().value(CookieConstant.NON_MEMBER_KEY, notNullValue()));
    }
  }

  @SuppressWarnings("unused")
  private static Stream<Arguments> givenVaildParameter_resultOk() {
    return Stream.of(
        Arguments.of(AddNonMemberUserAPIRequest.builder().name(Entity.createdVarChars[0]).build()));
  }
}
