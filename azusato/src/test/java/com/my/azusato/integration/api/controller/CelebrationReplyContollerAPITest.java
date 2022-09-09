package com.my.azusato.integration.api.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import com.my.azusato.api.controller.CelebrationReplyControllerAPI;
import com.my.azusato.api.controller.request.AddCelebrationReplyAPIReqeust;
import com.my.azusato.common.TestConstant;
import com.my.azusato.common.TestConstant.Entity;
import com.my.azusato.common.TestLogin;
import com.my.azusato.entity.CelebrationEntity;
import com.my.azusato.entity.CelebrationReplyEntity;
import com.my.azusato.entity.UserEntity;
import com.my.azusato.exception.AzusatoException;
import com.my.azusato.exception.ErrorResponse;
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

    @ParameterizedTest
    @MethodSource("com.my.azusato.common.TestSource#locales")
    public void givenNoCelebrationdata_result400(Locale locale) throws Exception {
      Path initFilePath =
          Paths.get(AddReplyCelebration.RESOUCE_PATH, "2", TestConstant.INIT_XML_FILE_NAME);
      dbUnitCompo.initalizeTable(initFilePath);

      MvcResult mvcResult = mockMvc
          .perform(MockMvcRequestBuilders
              .post(TestConstant.MAKE_ABSOLUTE_URL + Api.COMMON_REQUSET
                  + CelebrationReplyControllerAPI.COMMON_URL + "/" + "1000")
              .with(csrf()).content(getRequestBody())
              .contentType(HttpConstant.DEFAULT_CONTENT_TYPE_STRING)
              .with(user(TestLogin.adminLoginUser())).locale(locale))
          .andDo(print()).andExpect(status().is(400)).andReturn();

      String resultBody =
          mvcResult.getResponse().getContentAsString(Charset.forName(TestConstant.DEFAULT_CHARSET));
      ErrorResponse result = om.readValue(resultBody, ErrorResponse.class);
      String tableName = messageSource.getMessage(CelebrationEntity.TABLE_NAME_KEY, null, locale);

      assertEquals(
          new ErrorResponse(AzusatoException.I0005,
              messageSource.getMessage(AzusatoException.I0005, new String[] {tableName}, locale)),
          result);
    }

    @ParameterizedTest
    @MethodSource("com.my.azusato.common.TestSource#locales")
    public void givenNoUserdata_result400(Locale locale) throws Exception {
      MvcResult mvcResult = mockMvc
          .perform(MockMvcRequestBuilders
              .post(TestConstant.MAKE_ABSOLUTE_URL + Api.COMMON_REQUSET
                  + CelebrationReplyControllerAPI.COMMON_URL + "/" + "1000")
              .with(csrf()).content(getRequestBody())
              .contentType(HttpConstant.DEFAULT_CONTENT_TYPE_STRING)
              .with(user(TestLogin.adminLoginUser())).locale(locale))
          .andDo(print()).andExpect(status().is(400)).andReturn();

      String resultBody =
          mvcResult.getResponse().getContentAsString(Charset.forName(TestConstant.DEFAULT_CHARSET));
      ErrorResponse result = om.readValue(resultBody, ErrorResponse.class);
      String tableName = messageSource.getMessage(UserEntity.TABLE_NAME_KEY, null, locale);

      assertEquals(
          new ErrorResponse(AzusatoException.I0005,
              messageSource.getMessage(AzusatoException.I0005, new String[] {tableName}, locale)),
          result);
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

    @ParameterizedTest
    @MethodSource("com.my.azusato.common.TestSource#locales")
    public void givenDifferenceUser_result400(Locale locale) throws Exception {
      Path initFilePath =
          Paths.get(DeleteCeleReplybration.RESOUCE_PATH, "2", TestConstant.INIT_XML_FILE_NAME);
      dbUnitCompo.initalizeTable(initFilePath);

      MvcResult mvcResult = mockMvc
          .perform(MockMvcRequestBuilders
              .delete(TestConstant.MAKE_ABSOLUTE_URL + Api.COMMON_REQUSET
                  + CelebrationReplyControllerAPI.COMMON_URL + "/" + CELEBRATION_REPLY_NO)
              .with(csrf()).locale(locale).with(user(TestLogin.adminLoginUser())))
          .andDo(print()).andExpect(status().isBadRequest()).andReturn();

      String resultBody =
          mvcResult.getResponse().getContentAsString(Charset.forName(TestConstant.DEFAULT_CHARSET));
      ErrorResponse result = om.readValue(resultBody, ErrorResponse.class);

      assertEquals(new ErrorResponse(AzusatoException.I0006,
          messageSource.getMessage(AzusatoException.I0006, null, locale)), result);
    }

    @ParameterizedTest
    @MethodSource("com.my.azusato.common.TestSource#locales")
    public void givenNodata_result400(Locale locale) throws Exception {
      MvcResult mvcResult = mockMvc
          .perform(MockMvcRequestBuilders
              .delete(TestConstant.MAKE_ABSOLUTE_URL + Api.COMMON_REQUSET
                  + CelebrationReplyControllerAPI.COMMON_URL + "/" + "1000")
              .with(csrf()).locale(locale).with(user(TestLogin.adminLoginUser())))
          .andDo(print()).andExpect(status().isBadRequest()).andReturn();

      String resultBody =
          mvcResult.getResponse().getContentAsString(Charset.forName(TestConstant.DEFAULT_CHARSET));
      ErrorResponse result = om.readValue(resultBody, ErrorResponse.class);
      String tableName =
          messageSource.getMessage(CelebrationReplyEntity.TABLE_NAME_KEY, null, locale);

      assertEquals(
          new ErrorResponse(AzusatoException.I0005,
              messageSource.getMessage(AzusatoException.I0005, new String[] {tableName}, locale)),
          result);
    }
  }

}
