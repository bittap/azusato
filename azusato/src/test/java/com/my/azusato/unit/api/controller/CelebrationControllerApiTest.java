package com.my.azusato.unit.api.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.stream.Stream;
import org.apache.commons.io.output.FileWriterWithEncoding;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Disabled;
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
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.my.azusato.anonotation.UnitController;
import com.my.azusato.api.controller.CelebrationControllerAPI;
import com.my.azusato.api.controller.request.MyPageControllerRequest;
import com.my.azusato.api.service.CelebrationServiceAPI;
import com.my.azusato.api.service.request.GetCelebrationsSerivceAPIRequset;
import com.my.azusato.common.TestConstant;
import com.my.azusato.common.TestConstant.Entity;
import com.my.azusato.common.TestLogin;
import com.my.azusato.common.TestStream;
import com.my.azusato.exception.AzusatoException;
import com.my.azusato.exception.ErrorResponse;
import com.my.azusato.login.LoginUser;
import com.my.azusato.property.CelebrationProperty;
import com.my.azusato.view.controller.common.HttpConstant;
import com.my.azusato.view.controller.common.UrlConstant.Api;
import com.my.azusato.view.controller.common.ValueConstant;

@UnitController
@Import(value = CelebrationControllerAPI.class)
class CelebrationControllerApiTest {

  @Autowired
  MockMvc mockMvc;

  ObjectMapper om = new ObjectMapper();

  @MockBean
  CelebrationServiceAPI mockCeleAPIService;

  @Nested
  @DisplayName("結合テストで対応")
  class celebation {

  }

  @Nested
  class celebationContent {

    @Nested
    @DisplayName("正常系")
    @Disabled("結合テストで対応")
    class normal {

    }

    @Nested
    @DisplayName("準正常系")
    class subnormal {

      @ParameterizedTest
      @MethodSource("com.my.azusato.common.TestSource#I0008_Message")
      @DisplayName("パスの値が異常の場合_結果400をスローする")
      public void givenPathValueTypeError_result400(Locale locale, String expectMessage)
          throws Exception {
        MvcResult mvcResult = mockMvc
            .perform(MockMvcRequestBuilders
                .get(TestConstant.MAKE_ABSOLUTE_URL + Api.COMMON_REQUSET
                    + CelebrationControllerAPI.COMMON_URL + "/content/" + "string")
                .accept(HttpConstant.DEFAULT_CONTENT_TYPE).locale(locale))
            .andExpect(status().isBadRequest()).andReturn();

        String resultBody = mvcResult.getResponse()
            .getContentAsString(Charset.forName(TestConstant.DEFAULT_CHARSET));
        ErrorResponse result = om.readValue(resultBody, ErrorResponse.class);

        assertEquals(new ErrorResponse(AzusatoException.I0008, expectMessage), result);
      }
    }
  }

  @Nested
  class celebationContentResource {

    @Nested
    @DisplayName("正常系")
    class normal {

      @Autowired
      CelebrationProperty celeProperty;

      @Test
      @DisplayName("小さいファイルがある場合_結果ファイルのコンテンツを返す")
      public void givenNormalCase_WhenSmallFile_result200() throws Exception {
        String fileName = createSmallFile();

        mockMvc
            .perform(MockMvcRequestBuilders.get(TestConstant.MAKE_ABSOLUTE_URL + Api.COMMON_REQUSET
                + CelebrationControllerAPI.CELEBRATION_CONTENT_RESOUCE + "/" + fileName))
            .andDo(print()).andExpect(status().isOk()).andReturn();


        // 結果テストに関してはOutputstreamにあるデータをどうやって取得すればいいのか分からなくて諦めました。
      }

      @Test
      @DisplayName("大きいファイルがある場合_結果ファイルのコンテンツを返す")
      public void givenNormalCase_WhenLargeFile_result200() throws Exception {
        String fileName = createLargeFile();
        mockMvc
            .perform(MockMvcRequestBuilders.get(TestConstant.MAKE_ABSOLUTE_URL + Api.COMMON_REQUSET
                + CelebrationControllerAPI.CELEBRATION_CONTENT_RESOUCE + "/" + fileName))
            .andDo(print()).andExpect(status().isOk()).andReturn();


        // 結果テストに関してはOutputstreamにあるデータをどうやって取得すればいいのか分からなくて諦めました。
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
        Writer wi =
            new FileWriterWithEncoding(writedPath.toString(), ValueConstant.DEFAULT_CHARSET);
        wi.write(filePath);
        return fileName;
      }
    }

    @Nested
    @DisplayName("準正常系")
    class subnormal {

      @ParameterizedTest
      @MethodSource("com.my.azusato.common.TestSource#E0002_Message")
      @DisplayName("パラメータにあったパスにファイルが存在しない_結果404を返す")
      public void givenNoExistFilePathWithParameter_result404(Locale locale, String expectMessage)
          throws Exception {
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders
            .get(TestConstant.MAKE_ABSOLUTE_URL + Api.COMMON_REQUSET
                + CelebrationControllerAPI.CELEBRATION_CONTENT_RESOUCE + "/" + "noExistFile")
            .locale(locale)).andExpect(status().isNotFound()).andReturn();

        String resultBody = mvcResult.getResponse()
            .getContentAsString(Charset.forName(TestConstant.DEFAULT_CHARSET));
        ErrorResponse result = om.readValue(resultBody, ErrorResponse.class);

        assertEquals(new ErrorResponse(AzusatoException.E0002, expectMessage), result);
      }
    }
  }

  @Nested
  class celebrations {

    @Nested
    @DisplayName("正常系")
    class normal {

      final String pageOfElement = "5";
      final String pagesOfpage = "3";

      @Test
      @DisplayName("ページ番号がパラメータにない場合_ページ番号1で実行される")
      public void givenNoCurrentPageNo_Excuted1CurrentPageNo() throws Exception {
        MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
        parameters.add("pagesOfpage", pagesOfpage);
        parameters.add("pageOfElement", pageOfElement);
        GetCelebrationsSerivceAPIRequset mockParam = GetCelebrationsSerivceAPIRequset.builder()
            .pageReq(MyPageControllerRequest.builder() //
                .currentPageNo(1) //
                .pageOfElement(Integer.parseInt(pageOfElement)) //
                .pagesOfpage(Integer.parseInt(pagesOfpage)) //
                .build())
            .build();

        when(mockCeleAPIService.getCelebrations(mockParam)).thenReturn(null);

        mockMvc
            .perform(MockMvcRequestBuilders
                .get(TestConstant.MAKE_ABSOLUTE_URL + Api.COMMON_REQUSET
                    + CelebrationControllerAPI.CELEBRATIONS_URL)
                .contentType(HttpConstant.DEFAULT_CONTENT_TYPE)
                .accept(HttpConstant.DEFAULT_CONTENT_TYPE).params(parameters))
            .andExpect(status().isOk());

        Mockito.verify(mockCeleAPIService).getCelebrations(mockParam);
      }
    }

    @Nested
    @DisplayName("準正常系")
    class subnormal {

      @ParameterizedTest
      @MethodSource("com.my.azusato.unit.api.controller.CelebrationControllerApiTest#celebrations_givenParameterError_resultThrow400")
      @DisplayName("パラメータエラー_結果400エラーをスローする")
      public void givenParameterError_resultThrow400(Locale locale,
          MultiValueMap<String, String> parameters, String expect) throws Exception {
        MvcResult mvcResult = mockMvc
            .perform(MockMvcRequestBuilders
                .get(TestConstant.MAKE_ABSOLUTE_URL + Api.COMMON_REQUSET
                    + CelebrationControllerAPI.CELEBRATIONS_URL)
                .contentType(HttpConstant.DEFAULT_CONTENT_TYPE) //
                .accept(HttpConstant.DEFAULT_CONTENT_TYPE) //
                .params(parameters) //
                .locale(locale)) //
            .andExpect(status().isBadRequest()).andReturn();

        String resultBody = mvcResult.getResponse()
            .getContentAsString(Charset.forName(TestConstant.DEFAULT_CHARSET));
        ErrorResponse result = om.readValue(resultBody, ErrorResponse.class);

        assertEquals(new ErrorResponse(AzusatoException.I0004, expect), result);
      }

    }
  }

  @Nested
  class add {

    @Nested
    @DisplayName("正常系")
    class normal {

      @Test
      @DisplayName("ログイン権限が管理者の場合_addCelebartionAdminが実行される")
      void givenAdminAuthority_ExecutedMethodaddCelebartionAdmin() throws Exception {
        doNothing().when(mockCeleAPIService).addCelebartionAdmin(Mockito.any(), Mockito.any());

        mockMvc
            .perform(multipart(TestConstant.MAKE_ABSOLUTE_URL + Api.COMMON_REQUSET
                + CelebrationControllerAPI.COMMON_URL) //
                    .file(getMultiPartFile()) //
                    .with(user(TestLogin.adminLoginUser()))
                    .params(getParams(Entity.updatedVarChars[0], Entity.createdVarChars[0])) //
                    .with(csrf())) //
            .andDo(print()) //
            .andExpect(status().isCreated()) //
            .andReturn(); //

        verify(mockCeleAPIService).addCelebartionAdmin(Mockito.any(), Mockito.any());
      }

      @ParameterizedTest
      @MethodSource("com.my.azusato.common.TestSource#notAdminLogin")
      @DisplayName("ログイン権限が管理者以外の場合_addCelebartionが実行される")
      void givenNotAdminAuthority_ExecutedMethodaddCelebartion(LoginUser loginUser)
          throws Exception {
        doNothing().when(mockCeleAPIService).addCelebartion(Mockito.any(), Mockito.any());

        mockMvc
            .perform(multipart(TestConstant.MAKE_ABSOLUTE_URL + Api.COMMON_REQUSET
                + CelebrationControllerAPI.COMMON_URL) //
                    .file(getMultiPartFile()) //
                    .with(user(loginUser))
                    .params(getParams(Entity.updatedVarChars[0], Entity.createdVarChars[0])) //
                    .with(csrf())) //
            .andDo(print()) //
            .andExpect(status().isCreated()) //
            .andReturn(); //

        verify(mockCeleAPIService).addCelebartion(Mockito.any(), Mockito.any());
      }
    }

    @Nested
    @DisplayName("準正常系")
    class subnormal {

      @ParameterizedTest
      @MethodSource("com.my.azusato.common.TestSource#I0001_Message")
      @DisplayName("ログインしていない_結果401をスローする")
      public void givenNoLogin_resultThrow401(Locale locale, String expectMessage)
          throws Exception {

        MvcResult mvcResult = mockMvc
            .perform(multipart(TestConstant.MAKE_ABSOLUTE_URL + Api.COMMON_REQUSET
                + CelebrationControllerAPI.COMMON_URL) //
                    .file(getMultiPartFile()) //
                    .params(getParams(Entity.updatedVarChars[0], Entity.createdVarChars[0])) //
                    .with(csrf()) //
                    .locale(locale)) //
            .andDo(print()) //
            .andExpect(status().is(401)) //
            .andReturn(); //

        String resultBody = mvcResult.getResponse()
            .getContentAsString(Charset.forName(TestConstant.DEFAULT_CHARSET));
        ErrorResponse result = om.readValue(resultBody, ErrorResponse.class);

        assertEquals(new ErrorResponse(AzusatoException.I0001, expectMessage), result);
      }

      @ParameterizedTest
      @MethodSource("com.my.azusato.unit.api.controller.CelebrationControllerApiTest#add_givenAbnormalParameter_result400")
      public void givenAbnormalParameter_result400(Locale locale, MockMultipartFile multipartFile,
          MultiValueMap<String, String> params, String expectedMessage) throws Exception {
        MvcResult mvcResult = mockMvc
            .perform(multipart(TestConstant.MAKE_ABSOLUTE_URL + Api.COMMON_REQUSET
                + CelebrationControllerAPI.COMMON_URL).file(multipartFile).params(params)
                    .with(csrf()).locale(locale))
            .andDo(print()).andExpect(status().is(400)).andReturn();

        String resultBody = mvcResult.getResponse()
            .getContentAsString(Charset.forName(TestConstant.DEFAULT_CHARSET));
        ErrorResponse result = om.readValue(resultBody, ErrorResponse.class);

        assertEquals(new ErrorResponse(AzusatoException.I0004, expectedMessage), result);
      }
    }

    MockMultipartFile getMultiPartFile() throws Exception {
      return new MockMultipartFile("content", TestStream.getTestSmallCelebrationContentByte());
    }

    LinkedMultiValueMap<String, String> getParams(String name, String title) {
      LinkedMultiValueMap<String, String> params = new LinkedMultiValueMap<>();
      params.add("name", name);
      params.add("title", title);
      return params;
    }
  }

  @Nested
  class modify {

    final String CELEBRATION_NO = "1";

    @Nested
    @DisplayName("準正常系")
    class subnormal {

      @ParameterizedTest
      @MethodSource("com.my.azusato.common.TestSource#I0001_Message")
      @DisplayName("ログインしていない_結果401をスローする")
      void givenNoSession_result401(Locale locale, String expectMessage) throws Exception {
        MvcResult mvcResult = mockMvc.perform(getPutBuilder(CELEBRATION_NO) //
            .file(getMultiPartFile()) //
            .params(getParams()) //
            .with(csrf()) //
            .locale(locale)) //
            .andDo(print()) //
            .andExpect(status().is(401)) //
            .andReturn();

        String resultBody = mvcResult.getResponse()
            .getContentAsString(Charset.forName(TestConstant.DEFAULT_CHARSET));
        ErrorResponse result = om.readValue(resultBody, ErrorResponse.class);

        assertEquals(new ErrorResponse(AzusatoException.I0001, expectMessage), result);
      }

      @ParameterizedTest
      @MethodSource("com.my.azusato.common.TestSource#I0008_Message")
      void givenPathValueTypeError_result400(Locale locale, String expectMessage) throws Exception {

        MvcResult mvcResult = mockMvc.perform(getPutBuilder("String") //
            .file(getMultiPartFile()) //
            .params(getParams()) //
            .with(csrf()) //
            .locale(locale)) //
            .andDo(print()) //
            .andExpect(status().is(400)) //
            .andReturn();

        String resultBody = mvcResult.getResponse()
            .getContentAsString(Charset.forName(TestConstant.DEFAULT_CHARSET));
        ErrorResponse result = om.readValue(resultBody, ErrorResponse.class);

        assertEquals(new ErrorResponse(AzusatoException.I0008, expectMessage), result);
      }

      @ParameterizedTest
      @MethodSource("com.my.azusato.unit.api.controller.CelebrationControllerApiTest#modify_givenAbnormalParameter_result400")
      void givenParameterError_result400(Locale locale, MockMultipartFile multiPartFile,
          MultiValueMap<String, String> params, String expectedMessage) throws Exception {
        MvcResult mvcResult = mockMvc.perform(getPutBuilder(CELEBRATION_NO) //
            .file(multiPartFile) //
            .params(params) //
            .with(csrf()).locale(locale)) //
            .andDo(print()) //
            .andExpect(status().is(400)) //
            .andReturn(); //

        String resultBody = mvcResult.getResponse()
            .getContentAsString(Charset.forName(TestConstant.DEFAULT_CHARSET));
        ErrorResponse result = om.readValue(resultBody, ErrorResponse.class);

        assertEquals(new ErrorResponse(AzusatoException.I0004, expectedMessage), result);
      }

    }

    MockMultipartFile getMultiPartFile() throws Exception {
      return new MockMultipartFile("content", TestStream.getTestSmallCelebrationContentByte());
    }

    private MultiValueMap<String, String> getParams() throws Exception {
      MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
      params.add("title", Entity.updatedVarChars[2]);
      params.add("name", Entity.updatedVarChars[0]);

      return params;
    }

    private MockMultipartHttpServletRequestBuilder getPutBuilder(String celebrationNo) {
      // Multipartをputで実行するために
      MockMultipartHttpServletRequestBuilder builder =
          MockMvcRequestBuilders.multipart(TestConstant.MAKE_ABSOLUTE_URL + Api.COMMON_REQUSET
              + CelebrationControllerAPI.COMMON_URL + "/" + celebrationNo);
      builder.with(new RequestPostProcessor() {
        @Override
        public MockHttpServletRequest postProcessRequest(MockHttpServletRequest request) {
          request.setMethod("PUT");
          return request;
        }
      });
      return builder;
    }

  }

  @Nested
  class readCountUp {

    @Nested
    @DisplayName("準正常系")
    class subnormal {

      @ParameterizedTest
      @MethodSource("com.my.azusato.common.TestSource#I0008_Message")
      public void givenPathValueTypeError_result400(Locale locale, String expectMessage)
          throws Exception {
        MvcResult mvcResult = mockMvc
            .perform(MockMvcRequestBuilders
                .put(TestConstant.MAKE_ABSOLUTE_URL + Api.COMMON_REQUSET
                    + CelebrationControllerAPI.COMMON_URL + "/read-count-up" + "/" + "string")
                .with(csrf()).with(user(TestLogin.adminLoginUser())).locale(locale))
            .andDo(print()).andExpect(status().is(400)).andReturn();


        String resultBody = mvcResult.getResponse()
            .getContentAsString(Charset.forName(TestConstant.DEFAULT_CHARSET));
        ErrorResponse result = om.readValue(resultBody, ErrorResponse.class);

        assertEquals(new ErrorResponse(AzusatoException.I0008, expectMessage), result);
      }

    }
  }

  @Nested
  class delete {

    final String CELEBRATION_NO = "1";

    @ParameterizedTest
    @MethodSource("com.my.azusato.common.TestSource#I0001_Message")
    public void givenNoSession_result401(Locale locale, String expectMessage) throws Exception {
      MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders
          .delete(TestConstant.MAKE_ABSOLUTE_URL + Api.COMMON_REQUSET
              + CelebrationControllerAPI.COMMON_URL + "/" + CELEBRATION_NO)
          .with(csrf()).locale(locale)).andDo(print()).andExpect(status().is(401)).andReturn();

      String resultBody =
          mvcResult.getResponse().getContentAsString(Charset.forName(TestConstant.DEFAULT_CHARSET));
      ErrorResponse result = om.readValue(resultBody, ErrorResponse.class);

      assertEquals(new ErrorResponse(AzusatoException.I0001, expectMessage), result);
    }

    @ParameterizedTest
    @MethodSource("com.my.azusato.common.TestSource#I0008_Message")
    public void givenPathValueTypeError_result400(Locale locale, String expectMessage)
        throws Exception {
      MvcResult mvcResult = mockMvc
          .perform(MockMvcRequestBuilders
              .delete(TestConstant.MAKE_ABSOLUTE_URL + Api.COMMON_REQUSET
                  + CelebrationControllerAPI.COMMON_URL + "/" + "string")
              .with(csrf()).with(user(TestLogin.adminLoginUser())).locale(locale))
          .andDo(print()).andExpect(status().is(400)).andReturn();


      String resultBody =
          mvcResult.getResponse().getContentAsString(Charset.forName(TestConstant.DEFAULT_CHARSET));
      ErrorResponse result = om.readValue(resultBody, ErrorResponse.class);

      assertEquals(new ErrorResponse(AzusatoException.I0008, expectMessage), result);
    }

  }

  static Stream<Arguments> modify_givenAbnormalParameter_result400() throws Exception {
    add add = new CelebrationControllerApiTest().new add();
    return Stream.of(
        Arguments.of(TestConstant.LOCALE_JA, add.getMultiPartFile(),
            add.getParams(Entity.createdVarChars[0], null), "タイトルは必修項目です。"),
        Arguments.of(TestConstant.LOCALE_JA, add.getMultiPartFile(),
            add.getParams(Entity.createdVarChars[0], RandomStringUtils.randomAlphabetic(51)),
            "タイトルは最大50桁数まで入力可能です。"),
        Arguments.of(TestConstant.LOCALE_JA, new MockMultipartFile("noExistName", new byte[] {}),
            add.getParams(Entity.createdVarChars[0], Entity.createdVarChars[1]), "内容は必修項目です。"),
        Arguments.of(TestConstant.LOCALE_JA, add.getMultiPartFile(),
            add.getParams("", Entity.createdVarChars[0]), "名前は必修項目です。"),
        Arguments.of(TestConstant.LOCALE_KO, add.getMultiPartFile(),
            add.getParams(Entity.createdVarChars[0], null), "제목을 입력해주세요."),
        Arguments.of(TestConstant.LOCALE_KO, add.getMultiPartFile(),
            add.getParams(Entity.createdVarChars[0], RandomStringUtils.randomAlphabetic(51)),
            "글자 수 50을 초과해서 제목을 입력하는 것은 불가능합니다."),
        Arguments.of(TestConstant.LOCALE_KO, new MockMultipartFile("noExistName", new byte[] {}),
            add.getParams(Entity.createdVarChars[0], Entity.createdVarChars[1]), "내용을 입력해주세요."),
        Arguments.of(TestConstant.LOCALE_KO, add.getMultiPartFile(),
            add.getParams("", Entity.createdVarChars[0]), "이름을 입력해주세요."),
        Arguments.of(TestConstant.LOCALE_JA, new MockMultipartFile("noExistName", new byte[] {}),
            add.getParams("", ""), "タイトルは必修項目です。\n内容は必修項目です。\n名前は必修項目です。"));

  }

  static Stream<Arguments> add_givenAbnormalParameter_result400() throws Exception {
    add add = new CelebrationControllerApiTest().new add();
    return Stream.of(
        Arguments.of(TestConstant.LOCALE_JA, add.getMultiPartFile(),
            add.getParams(Entity.createdVarChars[0], null), "タイトルは必修項目です。"),
        Arguments.of(TestConstant.LOCALE_JA, add.getMultiPartFile(),
            add.getParams(Entity.createdVarChars[0], RandomStringUtils.randomAlphabetic(51)),
            "タイトルは最大50桁数まで入力可能です。"),
        Arguments.of(TestConstant.LOCALE_JA, new MockMultipartFile("noExistName", new byte[] {}),
            add.getParams(Entity.createdVarChars[0], Entity.createdVarChars[1]), "内容は必修項目です。"),
        Arguments.of(TestConstant.LOCALE_JA, add.getMultiPartFile(),
            add.getParams("", Entity.createdVarChars[0]), "名前は必修項目です。"),
        Arguments.of(TestConstant.LOCALE_KO, add.getMultiPartFile(),
            add.getParams(Entity.createdVarChars[0], null), "제목을 입력해주세요."),
        Arguments.of(TestConstant.LOCALE_KO, add.getMultiPartFile(),
            add.getParams(Entity.createdVarChars[0], RandomStringUtils.randomAlphabetic(51)),
            "글자 수 50을 초과해서 제목을 입력하는 것은 불가능합니다."),
        Arguments.of(TestConstant.LOCALE_KO, new MockMultipartFile("noExistName", new byte[] {}),
            add.getParams(Entity.createdVarChars[0], Entity.createdVarChars[1]), "내용을 입력해주세요."),
        Arguments.of(TestConstant.LOCALE_KO, add.getMultiPartFile(),
            add.getParams("", Entity.createdVarChars[0]), "이름을 입력해주세요."),
        Arguments.of(TestConstant.LOCALE_JA, new MockMultipartFile("noExistName", new byte[] {}),
            add.getParams("", ""), "タイトルは必修項目です。\n内容は必修項目です。\n名前は必修項目です。"));

  }

  static Stream<Arguments> celebrations_givenParameterError_resultThrow400() {
    final String FILED_NAME_currentPageNo = "currentPageNo";
    final String FILED_NAME_pagesOfpage = "pagesOfpage";
    final String FILED_NAME_pageOfElement = "pageOfElement";

    MultiValueMap<String, String> parameters1 = new LinkedMultiValueMap<String, String>();
    parameters1.add(FILED_NAME_currentPageNo, "string");
    parameters1.add(FILED_NAME_pagesOfpage, "1");
    parameters1.add(FILED_NAME_pageOfElement, "1");

    MultiValueMap<String, String> parameters2 = new LinkedMultiValueMap<String, String>();
    parameters2.add(FILED_NAME_pagesOfpage, null);
    parameters2.add(FILED_NAME_pageOfElement, "1");

    MultiValueMap<String, String> parameters3 = new LinkedMultiValueMap<String, String>();
    parameters3.add(FILED_NAME_pagesOfpage, "1");
    parameters3.add(FILED_NAME_pageOfElement, null);

    MultiValueMap<String, String> parameters4 = new LinkedMultiValueMap<String, String>();
    parameters4.add(FILED_NAME_pagesOfpage, "0");
    parameters4.add(FILED_NAME_pageOfElement, "1");

    MultiValueMap<String, String> parameters5 = new LinkedMultiValueMap<String, String>();
    parameters5.add(FILED_NAME_pagesOfpage, "1");
    parameters5.add(FILED_NAME_pageOfElement, "0");

    MultiValueMap<String, String> parameters6 = new LinkedMultiValueMap<String, String>();
    parameters6.add(FILED_NAME_pagesOfpage, "0");
    parameters6.add(FILED_NAME_pageOfElement, "0");


    return Stream.of(Arguments.of(TestConstant.LOCALE_JA, parameters1, "現在ページは数字のみ入力可能です。"),
        Arguments.of(TestConstant.LOCALE_JA, parameters2, "表示ページ数は必修項目です。"),
        Arguments.of(TestConstant.LOCALE_JA, parameters3, "ページのリスト表示数は必修項目です。"),
        Arguments.of(TestConstant.LOCALE_JA, parameters4, "最小1以上の表示ページ数を入力してください。"),
        Arguments.of(TestConstant.LOCALE_JA, parameters5, "最小1以上のページのリスト表示数を入力してください。"),
        Arguments.of(TestConstant.LOCALE_JA, parameters6,
            "最小1以上のページのリスト表示数を入力してください。\n最小1以上の表示ページ数を入力してください。"),
        Arguments.of(TestConstant.LOCALE_KO, parameters1, "현재페이지는 숫자만 입력가능합니다."),
        Arguments.of(TestConstant.LOCALE_KO, parameters2, "페이지표지수을 입력해주세요."),
        Arguments.of(TestConstant.LOCALE_KO, parameters3, "한 페이지표시 항목갯수을 입력해주세요."),
        Arguments.of(TestConstant.LOCALE_KO, parameters4, "최소1이상의 페이지표지수을 입력해주세요."),
        Arguments.of(TestConstant.LOCALE_KO, parameters5, "최소1이상의 한 페이지표시 항목갯수을 입력해주세요."),
        Arguments.of(TestConstant.LOCALE_KO, parameters6,
            "최소1이상의 페이지표지수을 입력해주세요.\n최소1이상의 한 페이지표시 항목갯수을 입력해주세요."));

  }

}
