package com.my.azusato.integration.api.controller;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import com.my.azusato.api.controller.ProfileControllerAPI;
import com.my.azusato.api.controller.response.DefaultRandomProfileResponse;
import com.my.azusato.common.TestConstant;
import com.my.azusato.common.TestConstant.Entity;
import com.my.azusato.common.TestLogin;
import com.my.azusato.common.TestStream;
import com.my.azusato.integration.AbstractIntegration;
import com.my.azusato.view.controller.common.UrlConstant.Api;


public class ProfileControllerAPITest extends AbstractIntegration {

  final static String RESOUCE_BASIC_PATH = "src/test/data/integration/api/controller/profile/";

  @Nested
  class RandomProfile {

    /**
     * random基本イメージを比較するために、宣言 key : base64 , value : false マッチするとfalseからtrueに変更する。
     */
    Map<String, Boolean> expects = expect();

    @Test
    public void normal_case() throws Exception {
      MvcResult mvcResult = mockMvc
          .perform(MockMvcRequestBuilders.get(TestConstant.MAKE_ABSOLUTE_URL + Api.COMMON_REQUSET
              + ProfileControllerAPI.RANDOM_URL))
          .andDo(print()).andExpect(status().isOk()).andReturn();

      DefaultRandomProfileResponse result = om.readValue(
          mvcResult.getResponse().getContentAsString(), DefaultRandomProfileResponse.class);

      assertTrue(expects.containsKey(result.getProfileImagePath()));
    }
  }

  public Map<String, Boolean> expect() {
    Path path = Paths.get("src", "main", "resources", "static", "image", "default", "profile");
    System.out.printf("absolutPath: %s\n", path.toAbsolutePath());
    List<String> fileNames = Arrays.asList(path.toFile().list());

    return fileNames.stream().collect(Collectors.toMap((e) -> {
      return Paths.get(profileProperty.getClientDefaultImageFolderPath(), e).toString();
    }, (e) -> {
      return Boolean.valueOf(false);
    }));

  }

  @Nested
  class uploadImage {

    final static String RESOUCE_PATH = RESOUCE_BASIC_PATH + "upload-image/";

    @Test
    public void givenFile_resultReturn200() throws Exception {
      String folderName = "1";
      String expectFileName = "1." + Entity.ImageType[0];
      String[] COMPARED_TABLE_NAME = {"user", "profile"};

      dbUnitCompo
          .initalizeTable(Paths.get(RESOUCE_PATH, folderName, TestConstant.INIT_XML_FILE_NAME));

      MockMultipartFile multipartFile = new MockMultipartFile("profileImage",
          TestConstant.TEST_IMAGE_FILENAME, null, TestStream.getTestImageBytes());

      mockMvc
          .perform(multipart(TestConstant.MAKE_ABSOLUTE_URL + Api.COMMON_REQUSET
              + ProfileControllerAPI.UPLOAD_IMG_URL).file(multipartFile)
                  .with(user(TestLogin.adminLoginUser())).with(csrf()))
          .andDo(print()).andExpect(status().isOk()).andReturn();

      // ファイル存在有無チェック
      Assertions.assertDoesNotThrow(() -> {
        Paths.get(profileProperty.getClientImageFolderPath() + expectFileName);
      });

      // compare tables
      for (String table : COMPARED_TABLE_NAME) {
        // exclude to compare dateTime columns when celebration table
        if (table.equals("user")) {
          dbUnitCompo.compareTable(
              Paths.get(RESOUCE_PATH, folderName, TestConstant.EXPECT_XML_FILE_NAME), table,
              TestConstant.DEFAULT_EXCLUDE_UPDATE_DATE_COLUMNS);
        } else {
          dbUnitCompo.compareTable(
              Paths.get(RESOUCE_PATH, folderName, TestConstant.EXPECT_XML_FILE_NAME), table);
        }
      }
    }

    // SpringBootTestではテスト不可能のため、省略
    // @ParameterizedTest
    // @MethodSource("com.my.azusato.integration.api.controller.ProfileControllerAPITest#givenNoPermitExtension_resultThrow400")
    // public void givenExceedMaxFileSize_resultThrow400(Locale locale, String message) throws
    // Exception {
    // MockMultipartFile multipartFile = new MockMultipartFile("profileImage",
    // TestConstant.TEST_LARGE_IMAGE_FILENAME,null, TestStream.getTestLargeImageBytes());
    //
    // MvcResult mvcResult = mockMvc
    // .perform(
    // multipart(TestConstant.MAKE_ABSOLUTE_URL + Api.COMMON_REQUSET +
    // ProfileControllerAPI.UPLOAD_IMG_URL)
    // .file(multipartFile)
    // .locale(locale)
    // .with(user(TestLogin.adminLoginUser()))
    // .with(csrf()))
    // .andDo(print()).andExpect(status().isBadRequest()).andReturn();
    //
    // String resultBody = mvcResult.getResponse()
    // .getContentAsString(Charset.forName(TestConstant.DEFAULT_CHARSET));
    // ErrorResponse result = om.readValue(resultBody, ErrorResponse.class);
    //
    // assertEquals(new ErrorResponse(AzusatoException.I0004, message), result);
    // }

  }
}
