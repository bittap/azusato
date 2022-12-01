package com.my.azusato.integration.api.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
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
import org.springframework.test.context.jdbc.Sql;
import com.my.azusato.anonotation.IntegrationService;
import com.my.azusato.api.controller.request.MyPageControllerRequest;
import com.my.azusato.api.service.CelebrationNoticeServiceAPI;
import com.my.azusato.api.service.request.GetCelebrationsSerivceAPIRequset;
import com.my.azusato.api.service.response.GetCelebrationNoticesSerivceAPIResponse;
import com.my.azusato.common.TestConstant;
import com.my.azusato.entity.CelebrationNoticeEntity;
import com.my.azusato.exception.AzusatoException;
import com.my.azusato.repository.CelebrationNoticeRepository;

@IntegrationService
@ExtendWith(MockitoExtension.class)
public class CelebrationNoticeServiceAPITest {

  @Autowired
  CelebrationNoticeServiceAPI target;

  @Autowired
  CelebrationNoticeRepository celeNotiRepo;

  final static String RESOUCE_BASIC_PATH =
      "src/test/data/integration/api/service/CelebrationNoticeServiceAPITest/";

  @Nested
  class celebrationNotices {

    long celeNo = 2L;

    long userNo = 2L;
    final int pageOfElement = 5;
    final int currentPageNo = 1;

    final static String RESOUCE_PATH = RESOUCE_BASIC_PATH + "celebrationNotices/";

    // TODO : コントローラーで値比較は対応。理由：Entityを返すように修正する予定のため
    // @Sql(scripts = "file:" + RESOUCE_PATH + "1/" + TestConstant.INIT_SQL_FILE_NAME)
    // @Test
    // void given2Data_resultReturn2Data_forComparingData() {
    // target.celebrationNotices(null, null)
    // int size = celeNotiRepo.findAll().size();
    // assertEquals(3, size);
    // }

    @Test
    void given0Data_resultReturn0Data() {
      GetCelebrationNoticesSerivceAPIResponse expected = GetCelebrationNoticesSerivceAPIResponse
          .builder().notices(List.of()).noReadLength(0).build();

      GetCelebrationNoticesSerivceAPIResponse actual =
          target.celebrationNotices(getGetCelebrationsSerivceAPIRequset(), 99999L);

      assertEquals(expected, actual);
    }

    @Test
    @Sql(scripts = "file:" + RESOUCE_PATH + "1/" + TestConstant.INIT_SQL_FILE_NAME)
    @DisplayName("2個のデータがある場合_参照フラグ昇順の2個のデータを返す(ソート確認)")
    void given2data_resultReturnReadedAsc2Data() throws Exception {
      GetCelebrationNoticesSerivceAPIResponse actual =
          target.celebrationNotices(getGetCelebrationsSerivceAPIRequset(), userNo);

      assertEquals(2, actual.getNotices().size());
      assertEquals(4L, actual.getNotices().get(0).getNo());
      assertEquals(3L, actual.getNotices().get(1).getNo());
    }

    @Test
    @Sql(scripts = "file:" + RESOUCE_PATH + "2/" + TestConstant.INIT_SQL_FILE_NAME)
    @DisplayName("2個のデータがある場合_お祝い番号降順の2個のデータを返す(ソート確認)")
    void given2data_resultReturnCelebrationNoDesc2Data() throws Exception {
      GetCelebrationNoticesSerivceAPIResponse actual =
          target.celebrationNotices(getGetCelebrationsSerivceAPIRequset(), userNo);

      assertEquals(2, actual.getNotices().size());
      assertEquals(4L, actual.getNotices().get(0).getNo());
      assertEquals(3L, actual.getNotices().get(1).getNo());
    }

    @Test
    @Sql(scripts = "file:" + RESOUCE_PATH + "3/" + TestConstant.INIT_SQL_FILE_NAME)
    @DisplayName("2個のデータがある場合_お祝い書き込み番号降順の2個のデータを返す(ソート確認)")
    void given2data_resultReturnCelebrationReplyNoDesc2Data() throws Exception {
      GetCelebrationNoticesSerivceAPIResponse actual =
          target.celebrationNotices(getGetCelebrationsSerivceAPIRequset(), userNo);

      assertEquals(2, actual.getNotices().size());
      assertEquals(4L, actual.getNotices().get(0).getNo());
      assertEquals(3L, actual.getNotices().get(1).getNo());
    }

    @Test
    @Sql(scripts = "file:" + RESOUCE_PATH + "4/" + TestConstant.INIT_SQL_FILE_NAME)
    @DisplayName("4個のデータがある場合_既読昇順&お祝い番号降順&お祝い書き込み番号降順の4個のデータを返す(ソート確認)")
    void given4data_resultReturnReadedAscCelebrationNoDescCelebrationReplyNoDesc2Data()
        throws Exception {
      GetCelebrationNoticesSerivceAPIResponse actual =
          target.celebrationNotices(getGetCelebrationsSerivceAPIRequset(), userNo);

      assertEquals(4, actual.getNotices().size());
      assertEquals(6L, actual.getNotices().get(0).getNo());
      assertEquals(5L, actual.getNotices().get(1).getNo());
      assertEquals(4L, actual.getNotices().get(2).getNo());
      assertEquals(3L, actual.getNotices().get(3).getNo());
    }

    @Test
    @Sql(scripts = "file:" + RESOUCE_PATH + "5/" + TestConstant.INIT_SQL_FILE_NAME)
    @DisplayName("6個のデータがある場合_5個のデータを返す")
    void given7data_resultReturn5Data() throws Exception {
      GetCelebrationNoticesSerivceAPIResponse actual =
          target.celebrationNotices(getGetCelebrationsSerivceAPIRequset(), userNo);

      assertEquals(5, actual.getNotices().size());
    }

    @Test
    @Sql(scripts = "file:" + RESOUCE_PATH + "6/" + TestConstant.INIT_SQL_FILE_NAME)
    @DisplayName("4個のデータがある&参照フラグ=falseは3個場合_")
    void given4data_resultReturnNoReadLengthIs3() throws Exception {
      GetCelebrationNoticesSerivceAPIResponse actual =
          target.celebrationNotices(getGetCelebrationsSerivceAPIRequset(), userNo);

      assertEquals(4, actual.getNotices().size());
      assertEquals(3, actual.getNoReadLength());
    }

    GetCelebrationsSerivceAPIRequset getGetCelebrationsSerivceAPIRequset() {
      return GetCelebrationsSerivceAPIRequset.builder().pageReq(MyPageControllerRequest.builder()
          .currentPageNo(currentPageNo).pageOfElement(pageOfElement).build()).build();
    }
  }



  @Nested
  class read {

    long celeNo = 1L;

    @Nested
    @DisplayName("正常系")
    class normal {

      long userNo = 1L;

      @Test
      public void resultReaded() throws Exception {
        // given
        LocalDateTime beforeExecuteTime = LocalDateTime.now();
        target.read(celeNo, userNo, null);

        // result
        List<CelebrationNoticeEntity> celebrationNotices =
            celeNotiRepo.findAllByCelebrationNoAndTargetUserNo(celeNo, userNo);

        for (CelebrationNoticeEntity celebrationNotice : celebrationNotices) {
          Assertions.assertTrue(celebrationNotice.getReaded());
          Assertions.assertTrue(beforeExecuteTime.isBefore(celebrationNotice.getReadDatetime()),
              String.format("比較 %s.before(%s)", beforeExecuteTime,
                  celebrationNotice.getReadDatetime()));
        }
      }
    }

    @Nested
    @DisplayName("準正常系")
    class subnormal {

      @ParameterizedTest
      @MethodSource("com.my.azusato.common.TestSource#I0005_celebrationNotice_Message")
      public void givenDifferenceUser_resultThrow(Locale locale, String expectedMessage)
          throws Exception {
        long differenceUser = 99999L;
        AzusatoException expected =
            new AzusatoException(HttpStatus.BAD_REQUEST, AzusatoException.I0005, expectedMessage);

        AzusatoException result = Assertions.assertThrows(AzusatoException.class, () -> {
          target.read(celeNo, differenceUser, locale);
        });

        assertEquals(expected, result);
      }
    }
  }

}
