package com.my.azusato.integration.api.controller;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import com.my.azusato.common.TestConstant;
import com.my.azusato.common.TestLogin;
import com.my.azusato.integration.AbstractIntegration;

public class CelebrationNoticeContollerAPITest extends AbstractIntegration {

  final static String RESOUCE_BASIC_PATH =
      "src/test/data/integration/CelebrationNoticeContollerAPI/";

  static final String COMMON_URL = "celebration-notice";


  @Nested
  class celebrationNotices {

    final static String RESOUCE_PATH = RESOUCE_BASIC_PATH + "celebrationNotices/";

    final String URL = COMMON_URL + "s";

    @Test
    @DisplayName("正常系_結果200")
    public void givenNoraml_result200() throws Exception {
      String folderName = "1";
      Path initFilePath = Paths.get(RESOUCE_PATH, folderName, TestConstant.INIT_XML_FILE_NAME);
      dbUnitCompo.initalizeTable(initFilePath);

      mockMvc.perform(MockMvcRequestBuilders
          .get(TestConstant.MAKE_ABSOLUTE_URL + TestConstant.API_REQUEST_URL + URL).with(csrf())
          .with(user(TestLogin.adminLoginUser()))).andDo(print()).andExpect(status().isOk());
    }
  }

  @Nested
  class read {

    final static String RESOUCE_PATH = RESOUCE_BASIC_PATH + "read/";

    final String CELEBRATION_NO = "1";

    final String READCOUNTUP_URL = COMMON_URL + "/read/" + CELEBRATION_NO;

    @Test
    @DisplayName("正常系_結果200")
    public void givenNoraml_result200() throws Exception {
      String folderName = "1";
      Path initFilePath = Paths.get(RESOUCE_PATH, folderName, TestConstant.INIT_XML_FILE_NAME);
      dbUnitCompo.initalizeTable(initFilePath);

      mockMvc
          .perform(MockMvcRequestBuilders
              .put(TestConstant.MAKE_ABSOLUTE_URL + TestConstant.API_REQUEST_URL + READCOUNTUP_URL)
              .with(csrf()).with(user(TestLogin.adminLoginUser())))
          .andDo(print()).andExpect(status().isOk());

    }
  }
}
