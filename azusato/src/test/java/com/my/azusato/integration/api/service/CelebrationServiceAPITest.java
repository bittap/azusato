package com.my.azusato.integration.api.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import com.my.azusato.anonotation.IntegrationService;
import com.my.azusato.api.service.CelebrationServiceAPI;
import com.my.azusato.api.service.request.AddCelebrationServiceAPIRequest;
import com.my.azusato.api.service.request.ModifyCelebationServiceAPIRequest;
import com.my.azusato.common.TestConstant;
import com.my.azusato.entity.CelebrationEntity;
import com.my.azusato.entity.CelebrationNoticeEntity;
import com.my.azusato.entity.UserEntity;
import com.my.azusato.exception.AzusatoException;
import com.my.azusato.property.CelebrationProperty;
import com.my.azusato.repository.CelebrationNoticeRepository;
import com.my.azusato.repository.CelebrationRepository;
import com.my.azusato.repository.UserRepository;

@IntegrationService
@ExtendWith(MockitoExtension.class)
public class CelebrationServiceAPITest {

  @Autowired
  CelebrationProperty celeProperty;

  @Autowired
  CelebrationServiceAPI target;

  @Autowired
  CelebrationRepository celeRepo;

  @Autowired
  CelebrationNoticeRepository celeNoticeRepo;

  @Autowired
  UserRepository userRepo;

  @Nested
  class addCelebration {

    @Nested
    @DisplayName("正常系")
    class normal {

      @Test
      void givenAdminUser_resultInseted() throws Exception {
        // given
        AddCelebrationServiceAPIRequest req = getVaildParameter();
        // when
        target.addCelebartionAdmin(req, null);

        // result
        CelebrationEntity inserted = celeRepo.findTopByOrderByNoDesc().get();
        compareCelebrationAndUser(inserted, req);
      }

      @Test
      @DisplayName("管理者意外で投稿&管理者存在_結果お祝いデータ&各お祝い通知テーブルに管理者をターゲットで挿入される")
      public void givenNotAdminWriterAndAdminExist_resultInsetedCelebrationAndCelebrationNotice()
          throws Exception {
        // given
        AddCelebrationServiceAPIRequest req = getVaildParameter();
        // when
        target.addCelebartion(req, null);

        // result
        CelebrationEntity inserted = celeRepo.findTopByOrderByNoDesc().get();
        compareCelebrationAndUser(inserted, req);
        // compare notice
        List<CelebrationNoticeEntity> insertedNotices = celeNoticeRepo.findAll();
        for (CelebrationNoticeEntity insertedNotice : insertedNotices) {
          Assertions.assertEquals(false, insertedNotice.getReaded());
          Assertions.assertTrue(userRepo
              .findByUserTypeAndCommonFlagDeleteFlag(UserEntity.Type.admin.toString(), false)
              .contains(insertedNotice.getTargetUser()), "各管理者をターゲットで登録される");
        }

      }

      AddCelebrationServiceAPIRequest getVaildParameter() throws FileNotFoundException {
        return AddCelebrationServiceAPIRequest.builder().name("name1").title("title1")
            .content(new FileInputStream(TestConstant.TEST_CELEBRATION_CONTENT_SMALL_PATH))
            .userNo(1L).build();
      }

      void compareCelebrationAndUser(CelebrationEntity inserted,
          AddCelebrationServiceAPIRequest req) {
        Assertions.assertEquals(req.getTitle(), inserted.getTitle());
        Assertions.assertEquals(req.getName(),
            inserted.getCommonUser().getCreateUserEntity().getName());
        contentPathCheck(inserted.getContentPath());
      }

      @AfterEach
      void deleteContentFloder() {
        File file1 = Paths.get(celeProperty.getServerContentFolderPath()).toFile();
        Arrays.asList(file1.listFiles()).forEach(File::delete);
      }
    }

    @Nested
    @DisplayName("準正常系")
    class subnormal {

      @ParameterizedTest
      @MethodSource("com.my.azusato.common.TestSource#I0005_user_Message")
      public void givenNotExistedUser_resultThrow(Locale locale, String expectedMessage)
          throws Exception {
        AzusatoException expected =
            new AzusatoException(HttpStatus.BAD_REQUEST, AzusatoException.I0005, expectedMessage);
        AddCelebrationServiceAPIRequest req = new AddCelebrationServiceAPIRequest();
        req.setUserNo(100000L);

        AzusatoException result = Assertions.assertThrows(AzusatoException.class, () -> {
          target.addCelebartion(req, locale);
        });

        assertEquals(expected, result);
      }
    }
  }

  @Nested
  class modifyCelebration {

    long userNo = 1L;

    long celebrationNo = 1L;

    @Nested
    @DisplayName("正常系")
    class normal {

      @Test
      public void resultUpdated() throws Exception {
        // given
        ModifyCelebationServiceAPIRequest req = getVaildParameter();
        // when
        CelebrationEntity actual = target.modifyCelebartion(req, null);

        // result
        Assertions.assertEquals(req.getTitle(), actual.getTitle());
        Assertions.assertEquals(req.getName(),
            actual.getCommonUser().getCreateUserEntity().getName());
        contentPathCheck(actual.getContentPath());
      }

      @AfterEach
      void deleteContentFloder() {
        File file1 = Paths.get(celeProperty.getServerContentFolderPath()).toFile();
        Arrays.asList(file1.listFiles()).forEach(File::delete);
      }
    }

    @Nested
    @DisplayName("準正常系")
    class subnormal {

      @ParameterizedTest
      @MethodSource("com.my.azusato.common.TestSource#I0005_celebration_Message")
      public void givenNotExistedCelebration_resultThrow(Locale locale, String expectedMessage)
          throws Exception {
        long notExistNo = 99999L;
        // given
        ModifyCelebationServiceAPIRequest req = getVaildParameter();
        req.setCelebationNo(notExistNo);

        AzusatoException expected =
            new AzusatoException(HttpStatus.BAD_REQUEST, AzusatoException.I0005, expectedMessage);

        AzusatoException result = Assertions.assertThrows(AzusatoException.class, () -> {
          target.modifyCelebartion(req, locale);
        });

        assertEquals(expected, result);
      }

      @ParameterizedTest
      @MethodSource("com.my.azusato.common.TestSource#I0006_Message")
      public void givenDifferenceUser_resultThrow(Locale locale, String expectedMessage)
          throws Exception {
        long differenceUser = 99999L;
        // given
        ModifyCelebationServiceAPIRequest req = getVaildParameter();
        req.setUserNo(differenceUser);

        AzusatoException expected =
            new AzusatoException(HttpStatus.BAD_REQUEST, AzusatoException.I0006, expectedMessage);

        AzusatoException result = Assertions.assertThrows(AzusatoException.class, () -> {
          target.modifyCelebartion(req, locale);
        });

        assertEquals(expected, result);
      }
    }

    ModifyCelebationServiceAPIRequest getVaildParameter() throws Exception {
      return ModifyCelebationServiceAPIRequest.builder().userNo(userNo).celebationNo(celebrationNo)
          .name("updatedName").title("updatedTitle")
          .content(new FileInputStream(TestConstant.TEST_CELEBRATION_CONTENT_SMALL_PATH)).build();
    }

  }

  @Nested
  class readCountUp {

    @Nested
    @DisplayName("正常系")
    class normal {

      long celeNo = 1L;

      @Test
      public void resultReadCountUp() throws Exception {
        // given
        CelebrationEntity celeEntity = celeRepo.findById(celeNo).get();
        int expectedReadCount = celeEntity.getReadCount() + 1;

        CelebrationEntity actual = target.readCountUp(celeNo, null);
        // result
        Assertions.assertEquals(expectedReadCount, actual.getReadCount());
      }
    }

    @Nested
    @DisplayName("準正常系")
    class subnormal {

      @ParameterizedTest
      @MethodSource("com.my.azusato.common.TestSource#I0005_celebration_Message")
      public void givenNotExistedCelebration_resultThrow(Locale locale, String expectedMessage)
          throws Exception {
        long notExistNo = 99999L;
        AzusatoException expected =
            new AzusatoException(HttpStatus.BAD_REQUEST, AzusatoException.I0005, expectedMessage);

        AzusatoException result = Assertions.assertThrows(AzusatoException.class, () -> {
          target.readCountUp(notExistNo, locale);
        });

        assertEquals(expected, result);
      }
    }
  }

  @Nested
  class deleteCelebation {

    long celeNo = 1L;

    long userNo = 1L;

    @Nested
    @DisplayName("正常系")
    class normal {
      @Test
      public void resultDeleted() throws Exception {
        // given
        target.deleteCelebartion(celeNo, userNo, null);

        // result
        CelebrationEntity actual = celeRepo.findById(celeNo).get();
        Assertions.assertEquals(true, actual.getCommonFlag().getDeleteFlag());
      }
    }

    @Nested
    @DisplayName("準正常系")
    class subnormal {

      @ParameterizedTest
      @MethodSource("com.my.azusato.common.TestSource#I0005_celebration_Message")
      public void givenNotExistedCelebration_resultThrow(Locale locale, String expectedMessage)
          throws Exception {
        long notExistNo = 99999L;
        AzusatoException expected =
            new AzusatoException(HttpStatus.BAD_REQUEST, AzusatoException.I0005, expectedMessage);

        AzusatoException result = Assertions.assertThrows(AzusatoException.class, () -> {
          target.deleteCelebartion(notExistNo, userNo, locale);
        });

        assertEquals(expected, result);
      }

      @ParameterizedTest
      @MethodSource("com.my.azusato.common.TestSource#I0006_Message")
      public void givenDifferenceUser_resultThrow(Locale locale, String expectedMessage)
          throws Exception {
        long differenceUser = 99999L;
        AzusatoException expected =
            new AzusatoException(HttpStatus.BAD_REQUEST, AzusatoException.I0006, expectedMessage);

        AzusatoException result = Assertions.assertThrows(AzusatoException.class, () -> {
          target.deleteCelebartion(celeNo, differenceUser, locale);
        });

        assertEquals(expected, result);
      }
    }
  }

  void contentPathCheck(String insetedContentPath) {
    Assertions.assertNotNull(insetedContentPath);
    Assertions.assertTrue(
        Files.exists(Paths.get(celeProperty.getServerContentFolderPath(), insetedContentPath)));
  }
}
