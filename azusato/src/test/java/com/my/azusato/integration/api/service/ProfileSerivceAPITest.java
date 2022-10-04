package com.my.azusato.integration.api.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.io.FileInputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import com.my.azusato.anonotation.IntegrationService;
import com.my.azusato.api.service.ProfileServiceAPI;
import com.my.azusato.api.service.request.ModifyUserProfileServiceAPIRequest;
import com.my.azusato.common.TestConstant;
import com.my.azusato.common.TestConstant.Entity;
import com.my.azusato.entity.UserEntity;
import com.my.azusato.exception.AzusatoException;
import com.my.azusato.factory.EntityFactory;
import com.my.azusato.property.ProfileProperty;
import com.my.azusato.repository.UserRepository;

@Import(ProfileServiceAPI.class)
@IntegrationService
public class ProfileSerivceAPITest {

  @Autowired
  ProfileServiceAPI targetService;

  @Autowired
  ProfileProperty profileProperty;

  @Autowired
  UserRepository userRepository;

  final String RESOUCE_BASIC_PATH = "src/test/data/unit/api/service/";

  @Nested
  class GetDefaultProfilePath {

    /**
     * およそ10000回を実行し、もし引っかからないものがあればfailにする。
     * 
     * @throws Exception
     */
    @Test
    public void getDefaultProfileBase64_normal_case() throws Exception {
      Map<String, Boolean> expect = expect();
      System.out.printf("expect : %s\n", expect);
      for (int i = 0; i < 10000; i++) {
        String result = targetService.getDefaultProfilePath();

        if (expect.containsKey(result)) {
          expect.put(result, true);
        }
      }

      expect.forEach((k, v) -> {
        Assertions.assertTrue(v);
      });
    }

  }

  @Nested
  class UpdateUserProfile {

    @Nested
    @DisplayName("正常系")
    class normal {

      @ParameterizedTest
      @MethodSource("com.my.azusato.integration.api.service.ProfileSerivceAPITest#UpdateUserProfile_normal_givenVaildImageType_resultUpdatedAndUploaded")
      public void givenVaildImageType_resultUpdatedAndUploaded(String imageType) throws Exception {
        UserEntity baseUserEntity = EntityFactory.userByNo1();
        String expectdFileName = baseUserEntity.getNo() + imageType;
        String expectedImagePath = "/" + baseUserEntity.getNo() + "." + imageType;

        ModifyUserProfileServiceAPIRequest req = ModifyUserProfileServiceAPIRequest.builder()
            .profileImage(new FileInputStream(TestConstant.TEST_IMAGE_PATH)) //
            .profileImageType(imageType).userNo(baseUserEntity.getNo()) //
            .build(); //

        targetService.updateUserProfile(req, TestConstant.LOCALE_JA);

        UserEntity result = userRepository.findById(baseUserEntity.getNo()).get();

        assertEquals(expectedImagePath, result.getProfile().getImagePath());
        Assertions.assertTrue(baseUserEntity.getCommonDate().getUpdateDatetime()
            .isBefore(result.getCommonDate().getUpdateDatetime()));

        // ファイル存在有無チェック
        Assertions.assertDoesNotThrow(() -> {
          Paths.get(profileProperty.getClientImageFolderPath() + expectdFileName);
        });
      }
    }

    @Nested
    @DisplayName("準正常系")
    class subnormal {
      @ParameterizedTest
      @MethodSource("com.my.azusato.common.TestSource#I0005_user_Message")
      public void givenNotExistUser_result400Error(Locale locale, String expectedMessage) {

        ModifyUserProfileServiceAPIRequest req =
            ModifyUserProfileServiceAPIRequest.builder().userNo(100000L).build();

        AzusatoException expected =
            new AzusatoException(HttpStatus.BAD_REQUEST, AzusatoException.I0005, expectedMessage);
        AzusatoException result = Assertions.assertThrows(AzusatoException.class,
            () -> targetService.updateUserProfile(req, locale));

        assertEquals(expected, result);

      }
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

  static List<String> UpdateUserProfile_normal_givenVaildImageType_resultUpdatedAndUploaded() {
    return List.of(Entity.ImageType[0], Entity.ImageType[1]);
  }
}
