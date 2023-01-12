package com.my.azusato.integration.api.controller;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import java.nio.file.Path;
import java.nio.file.Paths;
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

  final static String RESOUCE_BASIC_PATH = "src/test/data/integration/api/controller/";

  @Nested
  class AddReplyCelebration {

    final static String RESOUCE_PATH = RESOUCE_BASIC_PATH + "addCelebration-reply/";

    final String CELEBRATION_REPLY_NO = "1";

    @Test
    public void givenNoraml_result200() throws Exception {
      Path initFilePath =
          Paths.get(AddReplyCelebration.RESOUCE_PATH, "1", TestConstant.INIT_XML_FILE_NAME);
      Path expectFilePath =
          Paths.get(AddReplyCelebration.RESOUCE_PATH, "1", TestConstant.EXPECT_XML_FILE_NAME);
      String[] comparedTables =
          new String[] {"user", "celebration", "profile", "celebration_reply"};
      dbUnitCompo.initalizeTable(initFilePath);

      mockMvc
          .perform(MockMvcRequestBuilders
              .post(TestConstant.MAKE_ABSOLUTE_URL + Api.COMMON_REQUSET
                  + CelebrationReplyControllerAPI.COMMON_URL + "/" + CELEBRATION_REPLY_NO)
              .with(csrf()).content(getRequestBody())
              .contentType(HttpConstant.DEFAULT_CONTENT_TYPE_STRING)
              .with(user(TestLogin.adminLoginUser())))
          .andDo(print()).andExpect(status().isCreated());

      // compare tables
      for (String table : comparedTables) {
        // exclude to compare dateTime columns when celebration table
        if (table.equals("celebration_reply")) {
          dbUnitCompo.compareTable(expectFilePath, table, TestConstant.DEFAULT_EXCLUDE_COLUMNS);
        } else if (table.equals("user")) {
          dbUnitCompo.compareTable(expectFilePath, table,
              TestConstant.DEFAULT_EXCLUDE_UPDATE_DATE_COLUMNS);
        } else {
          dbUnitCompo.compareTable(expectFilePath, table);
        }
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

    final static String RESOUCE_PATH = RESOUCE_BASIC_PATH + "deleteCelebration-reply/";

    final String CELEBRATION_REPLY_NO = "1";

    @Test
    public void givenNoraml_result200() throws Exception {
      String folderName = "1";
      Path initFilePath = Paths.get(DeleteCeleReplybration.RESOUCE_PATH, folderName,
          TestConstant.INIT_XML_FILE_NAME);
      Path expectFilePath = Paths.get(DeleteCeleReplybration.RESOUCE_PATH, folderName,
          TestConstant.EXPECT_XML_FILE_NAME);
      String[] COMPARED_TABLE_NAME = {"user", "celebration", "profile", "celebration_reply"};

      dbUnitCompo.initalizeTable(initFilePath);

      mockMvc
          .perform(MockMvcRequestBuilders
              .delete(TestConstant.MAKE_ABSOLUTE_URL + Api.COMMON_REQUSET
                  + CelebrationReplyControllerAPI.COMMON_URL + "/" + CELEBRATION_REPLY_NO)
              .with(csrf()).with(user(TestLogin.adminLoginUser())))
          .andDo(print()).andExpect(status().isOk());

      // compare tables
      for (String table : COMPARED_TABLE_NAME) {
        // exclude to compare dateTime columns when celebration table
        if (table.equals("user")) {
          dbUnitCompo.compareTable(expectFilePath, table);
        } else {
          dbUnitCompo.compareTable(expectFilePath, table,
              TestConstant.DEFAULT_EXCLUDE_UPDATE_DATE_COLUMNS);
        }
      }
    }
  }

}
