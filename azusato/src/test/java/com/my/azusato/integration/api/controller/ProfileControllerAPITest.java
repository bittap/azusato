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
import com.my.azusato.common.TestStream;
import com.my.azusato.entity.ProfileEntity;
import com.my.azusato.entity.UserEntity;
import com.my.azusato.integration.AbstractIntegration;
import com.my.azusato.login.LoginUser;
import com.my.azusato.view.controller.common.UrlConstant.Api;


public class ProfileControllerAPITest extends AbstractIntegration {

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

    @Test
    public void givenFile_resultReturn200() throws Exception {
      UserEntity adminUser = userRepo.findById(Entity.ADMIN_USER_NOS[0]).get();
      String expectedFileName = "1." + Entity.ImageType[0];

      MockMultipartFile multipartFile = new MockMultipartFile("profileImage",
          TestConstant.TEST_IMAGE_FILENAME, null, TestStream.getTestImageBytes());

      mockMvc
          .perform(multipart(TestConstant.MAKE_ABSOLUTE_URL + Api.COMMON_REQUSET
              + ProfileControllerAPI.UPLOAD_IMG_URL).file(multipartFile)
                  .with(user(new LoginUser(adminUser))).with(csrf()))
          .andDo(print()).andExpect(status().isOk()).andReturn();

      ProfileEntity result = profileRepo.findById(adminUser.getNo()).get();

      // ファイル存在有無チェック
      Assertions.assertDoesNotThrow(() -> {
        Paths.get(profileProperty.getClientImageFolderPath() + expectedFileName);
      });

      Assertions.assertEquals("/" + expectedFileName, result.getImagePath());
    }
  }
}
