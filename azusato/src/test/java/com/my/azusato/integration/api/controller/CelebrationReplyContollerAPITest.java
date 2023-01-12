package com.my.azusato.integration.api.controller;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import com.my.azusato.api.controller.CelebrationReplyControllerAPI;
import com.my.azusato.api.controller.request.AddCelebrationReplyAPIReqeust;
import com.my.azusato.common.TestConstant;
import com.my.azusato.common.TestConstant.Entity;
import com.my.azusato.common.TestLogin;
import com.my.azusato.integration.AbstractIntegration;
import com.my.azusato.view.controller.common.HttpConstant;
import com.my.azusato.view.controller.common.UrlConstant.Api;

public class CelebrationReplyContollerAPITest extends AbstractIntegration {

  @Nested
  class AddReplyCelebration {

    final String CELEBRATION_NO = "1";

    @Test
    public void givenNoraml_result200() throws Exception {
      mockMvc
          .perform(MockMvcRequestBuilders
              .post(TestConstant.MAKE_ABSOLUTE_URL + Api.COMMON_REQUSET
                  + CelebrationReplyControllerAPI.COMMON_URL + "/" + CELEBRATION_NO)
              .with(csrf()).content(getRequestBody())
              .contentType(HttpConstant.DEFAULT_CONTENT_TYPE_STRING)
              .with(user(TestLogin.adminLoginUser())))
          .andDo(print()).andExpect(status().isCreated());
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

    @Test
    public void givenNoraml_result200() throws Exception {
      mockMvc
          .perform(MockMvcRequestBuilders
              .delete(TestConstant.MAKE_ABSOLUTE_URL + Api.COMMON_REQUSET
                  + CelebrationReplyControllerAPI.COMMON_URL + "/" + CELEBRATION_REPLY_NO)
              .with(csrf()).with(user(TestLogin.adminLoginUser())))
          .andDo(print()).andExpect(status().isOk());


    }
  }

}
