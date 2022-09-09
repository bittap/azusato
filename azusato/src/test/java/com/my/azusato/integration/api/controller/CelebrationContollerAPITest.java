package com.my.azusato.integration.api.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;
import org.apache.commons.io.output.FileWriterWithEncoding;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import com.my.azusato.api.controller.CelebrationControllerAPI;
import com.my.azusato.api.service.response.GetCelebrationSerivceAPIResponse;
import com.my.azusato.api.service.response.GetCelebrationsSerivceAPIResponse;
import com.my.azusato.api.service.response.GetCelebrationsSerivceAPIResponse.Celebration;
import com.my.azusato.common.TestConstant;
import com.my.azusato.common.TestConstant.Entity;
import com.my.azusato.common.TestLogin;
import com.my.azusato.common.TestStream;
import com.my.azusato.entity.CelebrationEntity;
import com.my.azusato.exception.AzusatoException;
import com.my.azusato.exception.ErrorResponse;
import com.my.azusato.integration.AbstractIntegration;
import com.my.azusato.page.MyPageResponse;
import com.my.azusato.view.controller.common.HttpConstant;
import com.my.azusato.view.controller.common.UrlConstant.Api;
import com.my.azusato.view.controller.common.ValueConstant;

public class CelebrationContollerAPITest extends AbstractIntegration {

  final static String RESOUCE_BASIC_PATH = "src/test/data/integration/api/controller/";

  @Nested
  class AddCelebration {

    final static String RESOUCE_PATH = RESOUCE_BASIC_PATH + "addCelebration/";

    @ParameterizedTest
    @MethodSource("com.my.azusato.integration.api.controller.CelebrationContollerAPITest#addCelebration_normal_case")
    public void normal_case_admin(UserDetails loginUser, Path initFilePath, Path expectFilePath)
        throws Exception {
      dbUnitCompo.initalizeTable(initFilePath);

      mockMvc
          .perform(multipart(TestConstant.MAKE_ABSOLUTE_URL + Api.COMMON_REQUSET
              + CelebrationControllerAPI.COMMON_URL).file(getMultiPartFile()).params(getParams())
                  .with(user(loginUser)).with(csrf()))
          .andDo(print()).andExpect(status().isCreated());

      String[] comparedTables = new String[] {"user", "celebration"};
      // compare tables
      for (String table : comparedTables) {
        // exclude to compare dateTime columns when celebration table
        if (table.equals("celebration")) {
          dbUnitCompo.compareTable(expectFilePath, table,
              TestConstant.DEFAULT_CELEBRATION_EXCLUDE_COLUMNS);
        } else if (table.equals("user") || table.equals("profile")) {
          dbUnitCompo.compareTable(expectFilePath, table,
              TestConstant.DEFAULT_EXCLUDE_DATE_COLUMNS);
        } else {
          dbUnitCompo.compareTable(expectFilePath, table);
        }
      }

      contentPathCheck();
    }

    private MultiValueMap<String, String> getParams() throws Exception {
      MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
      params.add("title", Entity.createdVarChars[0]);
      params.add("name", Entity.updatedVarChars[0]);

      return params;
    }
  }

  @Nested
  class ModifyCelebration {

    final static String RESOUCE_PATH = RESOUCE_BASIC_PATH + "modifyCelebration/";

    final String CELEBRATION_NO = "1";

    @Test
    public void givenNoraml_result200() throws Exception {
      Path initFilePath =
          Paths.get(ModifyCelebration.RESOUCE_PATH, "1", TestConstant.INIT_XML_FILE_NAME);
      Path expectFilePath =
          Paths.get(ModifyCelebration.RESOUCE_PATH, "1", TestConstant.EXPECT_XML_FILE_NAME);
      String[] comparedTables = new String[] {"user", "celebration"};
      dbUnitCompo.initalizeTable(initFilePath);

      mockMvc
          .perform(getPutBuilder().file(getMultiPartFile()).params(getParams())
              .with(user(TestLogin.adminLoginUser())).with(csrf()))
          .andDo(print()).andExpect(status().isOk());
      // compare tables
      for (String table : comparedTables) {
        // exclude to compare dateTime columns when celebration table
        if (table.equals("celebration") || table.equals("user")) {
          dbUnitCompo.compareTable(expectFilePath, table,
              TestConstant.DEFAULT_EXCLUDE_UPDATE_DATE_COLUMNS);
        } else {
          dbUnitCompo.compareTable(expectFilePath, table);
        }
      }

      contentPathCheck();
    }

    @ParameterizedTest
    @MethodSource("com.my.azusato.common.TestSource#locales")
    public void givenDifferenceUser_result400(Locale locale) throws Exception {
      Path initFilePath =
          Paths.get(ModifyCelebration.RESOUCE_PATH, "2", TestConstant.INIT_XML_FILE_NAME);
      dbUnitCompo.initalizeTable(initFilePath);

      MvcResult mvcResult = mockMvc
          .perform(getPutBuilder().file(getMultiPartFile()).params(getParams())
              .with(user(TestLogin.adminLoginUser())).with(csrf()).locale(locale))
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
          .perform(getPutBuilder().file(getMultiPartFile()).params(getParams())
              .with(user(TestLogin.adminLoginUser())).with(csrf()).locale(locale))
          .andDo(print()).andExpect(status().isBadRequest()).andReturn();

      String resultBody =
          mvcResult.getResponse().getContentAsString(Charset.forName(TestConstant.DEFAULT_CHARSET));
      ErrorResponse result = om.readValue(resultBody, ErrorResponse.class);
      String tableName = messageSource.getMessage(CelebrationEntity.TABLE_NAME_KEY, null, locale);

      assertEquals(
          new ErrorResponse(AzusatoException.I0005,
              messageSource.getMessage(AzusatoException.I0005, new String[] {tableName}, locale)),
          result);
    }

    private MockMultipartHttpServletRequestBuilder getPutBuilder() {
      // Multipartをputで実行するために
      MockMultipartHttpServletRequestBuilder builder =
          MockMvcRequestBuilders.multipart(TestConstant.MAKE_ABSOLUTE_URL + Api.COMMON_REQUSET
              + CelebrationControllerAPI.COMMON_URL + "/" + CELEBRATION_NO);
      builder.with(new RequestPostProcessor() {
        @Override
        public MockHttpServletRequest postProcessRequest(MockHttpServletRequest request) {
          request.setMethod("PUT");
          return request;
        }
      });
      return builder;
    }

    private MultiValueMap<String, String> getParams() throws Exception {
      MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
      params.add("title", Entity.updatedVarChars[2]);
      params.add("name", Entity.updatedVarChars[0]);

      return params;
    }
  }

  @Nested
  class DeleteCelebration {

    final static String RESOUCE_PATH = RESOUCE_BASIC_PATH + "deleteCelebration/";

    final String CELEBRATION_NO = "1";

    @Test
    public void givenNoraml_result200() throws Exception {
      String folderName = "1";
      Path initFilePath =
          Paths.get(DeleteCelebration.RESOUCE_PATH, folderName, TestConstant.INIT_XML_FILE_NAME);
      Path expectFilePath =
          Paths.get(DeleteCelebration.RESOUCE_PATH, folderName, TestConstant.EXPECT_XML_FILE_NAME);
      String[] COMPARED_TABLE_NAME = {"user", "celebration", "profile", "celebration_reply"};

      dbUnitCompo.initalizeTable(initFilePath);

      mockMvc.perform(MockMvcRequestBuilders
          .delete(TestConstant.MAKE_ABSOLUTE_URL + Api.COMMON_REQUSET
              + CelebrationControllerAPI.COMMON_URL + "/" + CELEBRATION_NO)
          .with(csrf()).with(user(TestLogin.adminLoginUser()))).andDo(print())
          .andExpect(status().isOk());

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
          Paths.get(DeleteCelebration.RESOUCE_PATH, "2", TestConstant.INIT_XML_FILE_NAME);
      dbUnitCompo.initalizeTable(initFilePath);

      MvcResult mvcResult = mockMvc
          .perform(MockMvcRequestBuilders
              .delete(TestConstant.MAKE_ABSOLUTE_URL + Api.COMMON_REQUSET
                  + CelebrationControllerAPI.COMMON_URL + "/" + CELEBRATION_NO)
              .with(csrf()).with(user(TestLogin.adminLoginUser())).locale(locale))
          .andDo(print()).andExpect(status().is(400)).andReturn();

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
                  + CelebrationControllerAPI.COMMON_URL + "/" + "1000")
              .with(csrf()).with(user(TestLogin.adminLoginUser())).locale(locale))
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


  }

  @Nested
  class ReadCountupCelebration {

    final static String RESOUCE_PATH = RESOUCE_BASIC_PATH + "readCountUp/";

    final String CELEBRATION_NO = "1";

    @Test
    public void givenNoraml_result200() throws Exception {
      String folderName = "1";
      Path initFilePath = Paths.get(ReadCountupCelebration.RESOUCE_PATH, folderName,
          TestConstant.INIT_XML_FILE_NAME);
      Path expectFilePath = Paths.get(ReadCountupCelebration.RESOUCE_PATH, folderName,
          TestConstant.EXPECT_XML_FILE_NAME);
      dbUnitCompo.initalizeTable(initFilePath);

      mockMvc
          .perform(MockMvcRequestBuilders
              .put(TestConstant.MAKE_ABSOLUTE_URL + Api.COMMON_REQUSET
                  + CelebrationControllerAPI.COMMON_URL + "/read-count-up" + "/" + CELEBRATION_NO)
              .with(csrf()).with(user(TestLogin.adminLoginUser())))
          .andDo(print()).andExpect(status().isOk());

      dbUnitCompo.compareTable(expectFilePath);
    }

    @ParameterizedTest
    @MethodSource("com.my.azusato.common.TestSource#locales")
    public void givenNodata_result400(Locale locale) throws Exception {
      MvcResult mvcResult = mockMvc
          .perform(MockMvcRequestBuilders
              .put(TestConstant.MAKE_ABSOLUTE_URL + Api.COMMON_REQUSET
                  + CelebrationControllerAPI.COMMON_URL + "/read-count-up" + "/" + CELEBRATION_NO)
              .with(csrf()).with(user(TestLogin.adminLoginUser())).locale(locale))
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
  }

  @Nested
  class GetCelebration {

    final static String RESOUCE_PATH = RESOUCE_BASIC_PATH + "getCelebration/";

    final long CELEBRATION_NO = 1L;


    @Test
    public void givenNormal_result200() throws Exception {
      String folderName = "1";
      dbUnitCompo
          .initalizeTable(Paths.get(RESOUCE_PATH, folderName, TestConstant.INIT_XML_FILE_NAME));

      MvcResult mvcResult =
          mockMvc
              .perform(MockMvcRequestBuilders
                  .get(TestConstant.MAKE_ABSOLUTE_URL + Api.COMMON_REQUSET
                      + CelebrationControllerAPI.COMMON_URL + "/" + String.valueOf(CELEBRATION_NO))
                  .accept(HttpConstant.DEFAULT_CONTENT_TYPE))
              .andExpect(status().isOk()).andReturn();

      String strResult =
          mvcResult.getResponse().getContentAsString(Charset.forName(TestConstant.DEFAULT_CHARSET));
      GetCelebrationSerivceAPIResponse result =
          om.readValue(strResult, GetCelebrationSerivceAPIResponse.class);

      GetCelebrationSerivceAPIResponse expect =
          GetCelebrationSerivceAPIResponse.builder().celebrationNo(CELEBRATION_NO)
              .title(Entity.createdVarChars[0]).content(Entity.createdVarChars[1])
              .name(Entity.createdVarChars[2]).profileImagePath(Entity.createdVarChars[0]).build();

      assertEquals(expect, result);
    }

    @ParameterizedTest
    @MethodSource("com.my.azusato.common.TestSource#locales")
    public void givenNodata_result400(Locale locale) throws Exception {
      MvcResult mvcResult = mockMvc
          .perform(MockMvcRequestBuilders
              .get(TestConstant.MAKE_ABSOLUTE_URL + Api.COMMON_REQUSET
                  + CelebrationControllerAPI.COMMON_URL + "/1000")
              .accept(HttpConstant.DEFAULT_CONTENT_TYPE).locale(locale))
          .andExpect(status().isBadRequest()).andReturn();

      String resultBody =
          mvcResult.getResponse().getContentAsString(Charset.forName(TestConstant.DEFAULT_CHARSET));
      ErrorResponse result = om.readValue(resultBody, ErrorResponse.class);

      String tableName = messageSource.getMessage(CelebrationEntity.TABLE_NAME_KEY, null, locale);
      String message =
          messageSource.getMessage(AzusatoException.I0005, new String[] {tableName}, locale);

      assertEquals(new ErrorResponse(AzusatoException.I0005, message), result);
    }

    @ParameterizedTest
    @MethodSource("com.my.azusato.common.TestSource#locales")
    public void givenParameterError_result400(Locale locale) throws Exception {
      MvcResult mvcResult = mockMvc
          .perform(MockMvcRequestBuilders
              .get(TestConstant.MAKE_ABSOLUTE_URL + Api.COMMON_REQUSET
                  + CelebrationControllerAPI.COMMON_URL + "/string")
              .accept(HttpConstant.DEFAULT_CONTENT_TYPE).locale(locale))
          .andExpect(status().isBadRequest()).andReturn();

      String resultBody =
          mvcResult.getResponse().getContentAsString(Charset.forName(TestConstant.DEFAULT_CHARSET));
      ErrorResponse result = om.readValue(resultBody, ErrorResponse.class);

      String message =
          messageSource.getMessage(AzusatoException.I0008, new String[] {null}, locale);

      assertEquals(new ErrorResponse(AzusatoException.I0008, message), result);
    }
  }

  @Nested
  class GetCelebrationContentResouce {

    @Test
    public void givenNormalCase_WhenSmallFile_result200() throws Exception {
      String fileName = createSmallFile();

      mockMvc
          .perform(MockMvcRequestBuilders.get(TestConstant.MAKE_ABSOLUTE_URL + Api.COMMON_REQUSET
              + CelebrationControllerAPI.CELEBRATION_CONTENT_RESOUCE + "/" + fileName))
          .andDo(print()).andExpect(status().isOk()).andReturn();


      // 結果テストに関してはOutputstreamにあるデータをどうやって取得すればいいのか分からなくて諦めました。
    }

    @Test
    public void givenNormalCase_WhenLargeFile_result200() throws Exception {
      String fileName = createLargeFile();
      mockMvc
          .perform(MockMvcRequestBuilders.get(TestConstant.MAKE_ABSOLUTE_URL + Api.COMMON_REQUSET
              + CelebrationControllerAPI.CELEBRATION_CONTENT_RESOUCE + "/" + fileName))
          .andDo(print()).andExpect(status().isOk()).andReturn();


      // 結果テストに関してはOutputstreamにあるデータをどうやって取得すればいいのか分からなくて諦めました。
    }

    @ParameterizedTest
    @MethodSource("com.my.azusato.common.TestSource#locales")
    public void givenParameterError_result404(Locale locale) throws Exception {
      MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders
          .get(TestConstant.MAKE_ABSOLUTE_URL + Api.COMMON_REQUSET
              + CelebrationControllerAPI.CELEBRATION_CONTENT_RESOUCE + "/" + "noExistFile")
          .locale(locale)).andExpect(status().isNotFound()).andReturn();

      String resultBody =
          mvcResult.getResponse().getContentAsString(Charset.forName(TestConstant.DEFAULT_CHARSET));
      ErrorResponse result = om.readValue(resultBody, ErrorResponse.class);

      String contentFieldName = messageSource.getMessage("content", null, locale);
      String message =
          messageSource.getMessage(AzusatoException.E0002, new String[] {contentFieldName}, locale);

      assertEquals(new ErrorResponse(AzusatoException.E0002, message), result);
    }

    private String createSmallFile() throws Exception {
      return createFile(TestConstant.TEST_CELEBRATION_CONTENT_SMALL_PATH);
    }

    private String createLargeFile() throws Exception {
      return createFile(TestConstant.TEST_CELEBRATION_CONTENT_LARGE_PATH);
    }

    private String createFile(String filePath) throws Exception {
      String fileName = "test.txt";
      Path writedPath = Paths.get(celeProperty.getServerContentFolderPath(), fileName);
      Writer wi = new FileWriterWithEncoding(writedPath.toString(), ValueConstant.DEFAULT_CHARSET);
      wi.write(filePath);
      return fileName;
    }
  }

  @Nested
  class GetCelebrations {

    final static String RESOUCE_PATH = RESOUCE_BASIC_PATH + "getCelebrations/";

    final long LOGIN_USER_NO = 1L;

    final String pageOfElement = "5";
    final String pagesOfpage = "3";
    final String currentPageNo = "1";

    @Test
    public void givenNormalData_result200() throws Exception {
      String folderName = "1";
      dbUnitCompo
          .initalizeTable(Paths.get(RESOUCE_PATH, folderName, TestConstant.INIT_XML_FILE_NAME));

      MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
      parameters.add("currentPageNo", currentPageNo);
      parameters.add("pagesOfpage", pagesOfpage);
      parameters.add("pageOfElement", pageOfElement);

      MvcResult mvcResult = mockMvc
          .perform(MockMvcRequestBuilders
              .get(TestConstant.MAKE_ABSOLUTE_URL + Api.COMMON_REQUSET
                  + CelebrationControllerAPI.CELEBRATIONS_URL)
              .accept(HttpConstant.DEFAULT_CONTENT_TYPE).params(parameters))
          .andExpect(status().isOk()).andReturn();

      String strResult =
          mvcResult.getResponse().getContentAsString(Charset.forName(TestConstant.DEFAULT_CHARSET));
      GetCelebrationsSerivceAPIResponse result =
          om.readValue(strResult, GetCelebrationsSerivceAPIResponse.class);

      GetCelebrationsSerivceAPIResponse expect = GetCelebrationsSerivceAPIResponse.builder()
          .page(MyPageResponse.builder().currentPageNo(1).pages(List.of(1)).hasPrivious(false)
              .hasNext(false).totalPage(1).build())
          .celebrations(List.of(
              Celebration.builder().title(Entity.createdVarChars[2]).name(Entity.createdVarChars[2])
                  .profileImagePath(Entity.createdVarChars[0]).no(Entity.createdLongs[1])
                  .createdDatetime(Entity.createdDatetimes[1]).readCount(1).build(),
              Celebration.builder().title(Entity.createdVarChars[0]).name(Entity.createdVarChars[2])
                  .profileImagePath(Entity.createdVarChars[0]).no(Entity.createdLongs[0])
                  .createdDatetime(Entity.createdDatetimes[0]).readCount(0).build()))
          .build();

      assertEquals(expect, result);
    }
  }

  @SuppressWarnings("unused")
  private static Stream<Arguments> addCelebration_normal_case() {
    return Stream.of(
        // admin login
        Arguments.of(TestLogin.adminLoginUser(),
            Paths.get(AddCelebration.RESOUCE_PATH, "1", TestConstant.INIT_XML_FILE_NAME),
            Paths.get(AddCelebration.RESOUCE_PATH, "1", TestConstant.EXPECT_XML_FILE_NAME)),
        // not admin login
        Arguments.of(TestLogin.kakaoLoginUser(),
            Paths.get(AddCelebration.RESOUCE_PATH, "2", TestConstant.INIT_XML_FILE_NAME),
            Paths.get(AddCelebration.RESOUCE_PATH, "2", TestConstant.EXPECT_XML_FILE_NAME)),
        // nonmember login
        Arguments.of(TestLogin.nonmemberLoginUser(),
            Paths.get(AddCelebration.RESOUCE_PATH, "3", TestConstant.INIT_XML_FILE_NAME),
            Paths.get(AddCelebration.RESOUCE_PATH, "3", TestConstant.EXPECT_XML_FILE_NAME)));
  }


  private void contentPathCheck() {
    String insertedPath = celeRepo.findAll().get(Entity.GET_INDEXS[0]).getContentPath();
    Assertions.assertNotNull(insertedPath);
    Assertions.assertTrue(Files.exists(Paths.get(celeProperty.getServerContentFolderPath())));
  }


  private static MockMultipartFile getMultiPartFile() throws Exception {
    return new MockMultipartFile("content", TestStream.getTestSmallCelebrationContentByte());
  }
}
