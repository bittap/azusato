package com.my.azusato.integration.api.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.util.List;
import java.util.Locale;
import java.util.Set;
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
import org.springframework.test.util.ReflectionTestUtils;
import com.my.azusato.anonotation.IntegrationService;
import com.my.azusato.api.controller.request.AddCelebrationReplyAPIReqeust;
import com.my.azusato.api.service.CelebrationReplyServiceAPI;
import com.my.azusato.entity.CelebrationNoticeEntity;
import com.my.azusato.entity.CelebrationReplyEntity;
import com.my.azusato.entity.UserEntity;
import com.my.azusato.exception.AzusatoException;
import com.my.azusato.repository.CelebrationNoticeRepository;
import com.my.azusato.repository.CelebrationReplyRepository;
import com.my.azusato.repository.UserRepository;

@IntegrationService
@ExtendWith(MockitoExtension.class)
class CelebrationReplyServiceAPITest {

  @Autowired
  CelebrationReplyServiceAPI target;

  @Autowired
  CelebrationReplyRepository celeReplyRepo;

  @Autowired
  CelebrationNoticeRepository celeNotiRepo;

  @Autowired
  UserRepository userRepo;

  @Nested
  class AddCelebrationReply {

    @Nested
    @DisplayName("正常系")
    class normal {

      String name = "updName";

      String content = "content";

      long targetCelebrationNo = 1L;

      @Test
      @DisplayName("書き込み作成者が他人で書き込みが二つある場合_結果書き込み「三つ」と「通知」は二つ")
      void givenWriterSelfAndNoReply_resultReply1AndNoticeZero() {
        // given
        long writerUserNo = 3;
        AddCelebrationReplyAPIReqeust req =
            AddCelebrationReplyAPIReqeust.builder().name(name).content(content).build();

        // when
        target.addCelebartionReply(req, targetCelebrationNo, writerUserNo, null);

        // result
        UserEntity writer = userRepo.findById(writerUserNo).get();
        List<CelebrationReplyEntity> CelebrationReplys =
            celeReplyRepo.findByCelebrationNo(targetCelebrationNo);
        List<CelebrationNoticeEntity> CelebrationNotices =
            celeNotiRepo.findAllByCelebrationNo(targetCelebrationNo);

        Assertions.assertEquals(name, writer.getName());

        Assertions.assertEquals(3, CelebrationReplys.size());
        Assertions.assertEquals(content, CelebrationReplys.get(2).getContent());
        Assertions.assertEquals(writerUserNo,
            CelebrationReplys.get(2).getCommonUser().getCreateUserEntity().getNo());

        Assertions.assertEquals(2, CelebrationNotices.size());
        Assertions.assertEquals(1L, CelebrationNotices.get(0).getTargetUser().getNo());
        Assertions.assertEquals(2L, CelebrationNotices.get(1).getTargetUser().getNo());
        for (CelebrationNoticeEntity CelebrationNotice : CelebrationNotices) {
          Assertions.assertFalse(CelebrationNotice.getReaded());
        }
      }
    }

    @Nested
    @DisplayName("準正常系")
    class subnormal {

      @ParameterizedTest
      @MethodSource("com.my.azusato.common.TestSource#I0005_celebration_Message")
      public void givenNotExistCelebration_resultThrow(Locale locale, String expectedMessage)
          throws Exception {
        AzusatoException expected =
            new AzusatoException(HttpStatus.BAD_REQUEST, AzusatoException.I0005, expectedMessage);


        AzusatoException actual = Assertions.assertThrows(AzusatoException.class, () -> {
          target.addCelebartionReply(null, 99999L, 1L, locale);
        });

        assertEquals(expected, actual);
      }

      @ParameterizedTest
      @MethodSource("com.my.azusato.common.TestSource#I0005_user_Message")
      public void givenNotExistUser_resultThrow(Locale locale, String expectedMessage)
          throws Exception {
        AzusatoException expected =
            new AzusatoException(HttpStatus.BAD_REQUEST, AzusatoException.I0005, expectedMessage);


        AzusatoException actual = Assertions.assertThrows(AzusatoException.class, () -> {
          target.addCelebartionReply(null, 1L, 99999L, locale);
        });

        assertEquals(expected, actual);
      }
    }
  }

  @Nested
  class getNoitceTragetUsers {

    @Nested
    @DisplayName("正常系")
    class normal {

      UserEntity celebrationWriter = UserEntity.builder().no(1L).build();

      Set<UserEntity> fetchedReplyUsers =
          Set.of(UserEntity.builder().no(2L).build(), UserEntity.builder().no(3L).build());

      @Test
      @DisplayName("お祝い作成者と書き込み作成者が違う_結果作成者は含まれる")
      void givenDifferenceCelebrationUserAndCelebrationReplyUser_resultIncludeCelebrationUser() {
        // given
        UserEntity celebrationReplyWriter = UserEntity.builder().no(4L).build();
        Set<Long> expected = Set.of(celebrationWriter.getNo(), 2L, 3L);

        // when
        Set<UserEntity> actuals = ReflectionTestUtils.invokeMethod(target, "getNoitceTragetUsers",
            fetchedReplyUsers, celebrationReplyWriter, celebrationWriter);

        // result
        for (UserEntity actual : actuals) {
          Assertions.assertTrue(expected.contains(actual.getNo()),
              String.format("%sは%dを含めておりません。", expected, actual.getNo()));
        }
      }

      @Test
      @DisplayName("お祝い作成者と書き込み作成者が同じ_結果作成者と書き込み作成者は除外される")
      void givenSameCelebrationUserAndCelebrationReplyUser_resultIncludeCelebrationUser() {
        // given
        UserEntity celebrationReplyWriter = celebrationWriter;
        Set<Long> expected = Set.of(2L, 3L);

        // when
        Set<UserEntity> actuals = ReflectionTestUtils.invokeMethod(target, "getNoitceTragetUsers",
            fetchedReplyUsers, celebrationReplyWriter, celebrationWriter);

        // result
        for (UserEntity actual : actuals) {
          Assertions.assertTrue(expected.contains(actual.getNo()),
              String.format("%sは%dを含めておりません。", expected, actual.getNo()));
        }
      }
    }
  }

  @Nested
  class deleteCelebartionReply {

    long celeReplyNo = 1L;

    long userNo = 1L;

    @Nested
    @DisplayName("正常系")
    class normal {
      @Test
      public void resultDeleted() throws Exception {
        // given
        target.deleteCelebartionReply(celeReplyNo, userNo, null);

        // result
        CelebrationReplyEntity actual = celeReplyRepo.findById(celeReplyNo).get();
        Assertions.assertEquals(true, actual.getCommonFlag().getDeleteFlag());
      }
    }

    @Nested
    @DisplayName("準正常系")
    class subnormal {

      @ParameterizedTest
      @MethodSource("com.my.azusato.common.TestSource#I0005_celebrationReply_Message")
      public void givenNotExistedCelebration_resultThrow(Locale locale, String expectedMessage)
          throws Exception {
        long notExistNo = 99999L;
        AzusatoException expected =
            new AzusatoException(HttpStatus.BAD_REQUEST, AzusatoException.I0005, expectedMessage);

        AzusatoException result = Assertions.assertThrows(AzusatoException.class, () -> {
          target.deleteCelebartionReply(notExistNo, userNo, locale);
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
          target.deleteCelebartionReply(celeReplyNo, differenceUser, locale);
        });

        assertEquals(expected, result);
      }
    }
  }

}
