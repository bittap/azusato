package com.my.azusato.integration.api.controller;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import java.io.Writer;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;
import org.apache.commons.io.output.FileWriterWithEncoding;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import com.my.azusato.api.controller.CelebrationControllerAPI;
import com.my.azusato.common.TestConstant;
import com.my.azusato.common.TestConstant.Entity;
import com.my.azusato.common.TestStream;
import com.my.azusato.entity.UserEntity;
import com.my.azusato.integration.AbstractIntegrationForTest;
import com.my.azusato.login.LoginUser;
import com.my.azusato.view.controller.common.HttpConstant;
import com.my.azusato.view.controller.common.UrlConstant.Api;
import com.my.azusato.view.controller.common.ValueConstant;

public class CelebrationContollerAPITest extends AbstractIntegrationForTest {

  @Nested
  class AddCelebration {

    @ParameterizedTest
    @MethodSource("com.my.azusato.integration.api.controller.CelebrationContollerAPITest#AddCelebration_resultOK")
    public void resultOK(Long userNo) throws Exception {
      UserEntity loginUser = userRepo.findById(userNo).get();

      mockMvc
          .perform(multipart(TestConstant.MAKE_ABSOLUTE_URL + Api.COMMON_REQUSET
              + CelebrationControllerAPI.COMMON_URL).file(getMultiPartFile()).params(getParams())
                  .with(user(new LoginUser(loginUser))).with(csrf()))
          .andDo(print()).andExpect(status().isCreated());
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

    final String CELEBRATION_NO = "1";

    @Test
    public void givenNoraml_result200() throws Exception {
      UserEntity loginUser = userRepo.findById(Entity.ADMIN_USER_NOS[0]).get();

      mockMvc
          .perform(getPutBuilder().file(getMultiPartFile()).params(getParams())
              .with(user(new LoginUser(loginUser))).with(csrf()))
          .andDo(print()).andExpect(status().isOk());
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

    final String CELEBRATION_NO = "1";

    @Test
    public void givenNoraml_result200() throws Exception {
      UserEntity loginUser = userRepo.findById(Entity.ADMIN_USER_NOS[0]).get();

      mockMvc.perform(MockMvcRequestBuilders
          .delete(TestConstant.MAKE_ABSOLUTE_URL + Api.COMMON_REQUSET
              + CelebrationControllerAPI.COMMON_URL + "/" + CELEBRATION_NO)
          .with(csrf()).with(user(new LoginUser(loginUser)))).andDo(print())
          .andExpect(status().isOk());
    }
  }

  @Nested
  class ReadCountupCelebration {

    final String CELEBRATION_NO = "1";

    @Test
    public void givenNoraml_result200() throws Exception {
      UserEntity loginUser = userRepo.findById(Entity.ADMIN_USER_NOS[0]).get();

      mockMvc
          .perform(MockMvcRequestBuilders
              .put(TestConstant.MAKE_ABSOLUTE_URL + Api.COMMON_REQUSET
                  + CelebrationControllerAPI.COMMON_URL + "/read-count-up" + "/" + CELEBRATION_NO)
              .with(csrf()).with(user(new LoginUser(loginUser))))
          .andDo(print()).andExpect(status().isOk());
    }
  }

  @Nested
  class GetCelebration {

    final long CELEBRATION_NO = 1L;

    @Test
    public void givenNormal_result200() throws Exception {
      mockMvc.perform(MockMvcRequestBuilders
          .get(TestConstant.MAKE_ABSOLUTE_URL + Api.COMMON_REQUSET
              + CelebrationControllerAPI.COMMON_URL + "/" + String.valueOf(CELEBRATION_NO))
          .accept(HttpConstant.DEFAULT_CONTENT_TYPE)).andExpect(status().isOk());
    }
  }

  @Nested
  class GetCelebrationContentResouce {

    @Test
    public void givenNormalCase_result200() throws Exception {
      String fileName = createSmallFile();

      mockMvc
          .perform(MockMvcRequestBuilders.get(TestConstant.MAKE_ABSOLUTE_URL + Api.COMMON_REQUSET
              + CelebrationControllerAPI.CELEBRATION_CONTENT_RESOUCE + "/" + fileName))
          .andDo(print()).andExpect(status().isOk()).andReturn();


      // 結果テストに関してはOutputstreamにあるデータをどうやって取得すればいいのか分からなくて諦めました。
    }

    private String createSmallFile() throws Exception {
      return createFile(TestConstant.TEST_CELEBRATION_CONTENT_SMALL_PATH);
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

    final long LOGIN_USER_NO = 1L;

    final String pageOfElement = "5";
    final String pagesOfpage = "3";
    final String currentPageNo = "1";

    @Test
    public void givenNormalData_result200() throws Exception {
      MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
      parameters.add("currentPageNo", currentPageNo);
      parameters.add("pagesOfpage", pagesOfpage);
      parameters.add("pageOfElement", pageOfElement);

      mockMvc
          .perform(MockMvcRequestBuilders
              .get(TestConstant.MAKE_ABSOLUTE_URL + Api.COMMON_REQUSET
                  + CelebrationControllerAPI.CELEBRATIONS_URL)
              .accept(HttpConstant.DEFAULT_CONTENT_TYPE).params(parameters))
          .andExpect(status().isOk());
    }
  }

  @SuppressWarnings("unused")
  private static Stream<Long> AddCelebration_resultOK() {
    return Stream.of(Entity.ADMIN_USER_NOS[0], Entity.NONMEMBER_USER_NOS[0]);
  }


  private static MockMultipartFile getMultiPartFile() throws Exception {
    return new MockMultipartFile("content", TestStream.getTestSmallCelebrationContentByte());
  }
}
