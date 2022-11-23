package com.my.azusato.unit.api.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Locale;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import com.my.azusato.api.controller.request.MyPageControllerRequest;
import com.my.azusato.api.service.CelebrationServiceAPI;
import com.my.azusato.api.service.request.GetCelebrationsSerivceAPIRequset;
import com.my.azusato.api.service.response.GetCelebrationContentSerivceAPIResponse;
import com.my.azusato.api.service.response.GetCelebrationContentSerivceAPIResponse.CelebrationReply;
import com.my.azusato.api.service.response.GetCelebrationSerivceAPIResponse;
import com.my.azusato.api.service.response.GetCelebrationsSerivceAPIResponse;
import com.my.azusato.api.service.response.GetCelebrationsSerivceAPIResponse.Celebration;
import com.my.azusato.common.TestConstant;
import com.my.azusato.common.TestConstant.Entity;
import com.my.azusato.entity.CelebrationEntity;
import com.my.azusato.exception.AzusatoException;
import com.my.azusato.integration.AbstractIntegration;
import com.my.azusato.page.MyPageResponse;
import com.my.azusato.property.ProfileProperty;

public class CelebrationServiceAPITest extends AbstractIntegration {

  @Autowired
  CelebrationServiceAPI celeServiceAPI;

  final String RESOUCE_BASIC_PATH = "src/test/data/unit/api/service/";

  @Nested
  class GetCelebration {

    final String RESOUCE_PATH = RESOUCE_BASIC_PATH + "getCelebration/";

    @Test
    public void givenNormal_result200() throws Exception {
      String folderName = "1";
      long celebationNo = 1L;
      dbUnitCompo
          .initalizeTable(Paths.get(RESOUCE_PATH, folderName, TestConstant.INIT_XML_FILE_NAME));

      GetCelebrationSerivceAPIResponse result =
          celeServiceAPI.getCelebration(celebationNo, TestConstant.LOCALE_JA);

      GetCelebrationSerivceAPIResponse expect =
          GetCelebrationSerivceAPIResponse.builder().celebrationNo(celebationNo)
              .title(Entity.createdVarChars[0]).content(Entity.createdVarChars[1])
              .name(Entity.createdVarChars[2]).profileImagePath(Entity.createdVarChars[0]).build();

      assertEquals(expect, result);

    }

    @ParameterizedTest
    @MethodSource("com.my.azusato.common.TestSource#locales")
    public void givenNodata_result400(Locale locale) throws Exception {
      long celebationNo = 100000L;

      String tableName = messageSource.getMessage(CelebrationEntity.TABLE_NAME_KEY, null, locale);
      AzusatoException expect = new AzusatoException(HttpStatus.BAD_REQUEST, AzusatoException.I0005,
          messageSource.getMessage(AzusatoException.I0005, new String[] {tableName}, locale));

      AzusatoException result = Assertions.assertThrows(AzusatoException.class, () -> {
        celeServiceAPI.getCelebration(celebationNo, locale);
      });

      assertEquals(expect, result);
    }
  }

  @Nested
  class GetCelebrationContent {

    @Autowired
    ProfileProperty profileProperty;

    final String RESOUCE_PATH = RESOUCE_BASIC_PATH + "getCelebrationContent/";
    final long CELEBRATION_NO = 1L;
    final long LOGIN_USER_NO = 1L;

    final int pageOfElement = 5;
    final int pagesOfpage = 3;
    final int currentPageNo = 1;

    /**
     * お祝い書き込みリストのorderbyテスト
     * 
     * @throws Exception
     */
    @Test
    public void When2data_givenNoDesc_resultOrderdbyNoAsc() throws Exception {
      String folderName = "1";
      dbUnitCompo
          .initalizeTable(Paths.get(RESOUCE_PATH, folderName, TestConstant.INIT_XML_FILE_NAME));

      GetCelebrationContentSerivceAPIResponse response = celeServiceAPI
          .getCelebrationContent(CELEBRATION_NO, LOGIN_USER_NO, TestConstant.LOCALE_JA);


      GetCelebrationContentSerivceAPIResponse expect = GetCelebrationContentSerivceAPIResponse
          .builder().contentPath(Entity.createdVarChars[1]).no(Entity.createdLongs[0]).owner(true)
          .replys(List.of(
              CelebrationReply.builder().no(Entity.createdLongs[0])
                  .content(Entity.createdVarChars[0]).createdDatetime(Entity.createdDatetimes[0])
                  .name(Entity.createdVarChars[2]).profileImagePath(Entity.createdVarChars[0])
                  .owner(true).build(),
              CelebrationReply.builder().no(Entity.createdLongs[1])
                  .content(Entity.createdVarChars[1]).createdDatetime(Entity.createdDatetimes[1])
                  .name(Entity.createdVarChars[2]).profileImagePath(Entity.createdVarChars[0])
                  .owner(true).build()))
          .build();

      assertEquals(expect, response);
    }

    @Test
    public void givenNotOwner_resultOwnerFalse() throws Exception {
      String folderName = "2";
      dbUnitCompo
          .initalizeTable(Paths.get(RESOUCE_PATH, folderName, TestConstant.INIT_XML_FILE_NAME));

      GetCelebrationContentSerivceAPIResponse response = celeServiceAPI
          .getCelebrationContent(CELEBRATION_NO, LOGIN_USER_NO, TestConstant.LOCALE_JA);


      GetCelebrationContentSerivceAPIResponse expect = GetCelebrationContentSerivceAPIResponse
          .builder().contentPath(Entity.createdVarChars[1]).no(Entity.createdLongs[0]).owner(false)
          .replys(List.of(
              CelebrationReply.builder().no(Entity.createdLongs[0])
                  .content(Entity.createdVarChars[0]).createdDatetime(Entity.createdDatetimes[0])
                  .name(Entity.createdVarChars[2]).profileImagePath(Entity.createdVarChars[0])
                  .owner(false).build(),
              CelebrationReply.builder().no(Entity.createdLongs[1])
                  .content(Entity.createdVarChars[1]).createdDatetime(Entity.createdDatetimes[1])
                  .name(Entity.createdVarChars[2]).profileImagePath(Entity.createdVarChars[0])
                  .owner(true).build()))
          .build();

      assertEquals(expect, response);
    }

    @Test
    public void when2data_givenDeleted1data_result1Data() throws Exception {
      String folderName = "3";
      dbUnitCompo
          .initalizeTable(Paths.get(RESOUCE_PATH, folderName, TestConstant.INIT_XML_FILE_NAME));

      GetCelebrationContentSerivceAPIResponse response = celeServiceAPI
          .getCelebrationContent(CELEBRATION_NO, LOGIN_USER_NO, TestConstant.LOCALE_JA);


      GetCelebrationContentSerivceAPIResponse expect = GetCelebrationContentSerivceAPIResponse
          .builder().contentPath(Entity.createdVarChars[1]).no(Entity.createdLongs[0]).owner(true)
          .replys(List.of(CelebrationReply.builder().no(Entity.createdLongs[1])
              .content(Entity.createdVarChars[1]).createdDatetime(Entity.createdDatetimes[1])
              .name(Entity.createdVarChars[2]).profileImagePath(Entity.createdVarChars[0])
              .owner(true).build()))
          .build();

      assertEquals(expect, response);
    }

    @ParameterizedTest
    @MethodSource("com.my.azusato.common.TestSource#locales")
    public void givenNoCelebrationData_resultError(Locale locale) throws Exception {

      String tableName = messageSource.getMessage(CelebrationEntity.TABLE_NAME_KEY, null, locale);
      AzusatoException expect = new AzusatoException(HttpStatus.BAD_REQUEST, AzusatoException.I0005,
          messageSource.getMessage(AzusatoException.I0005, new String[] {tableName}, locale));

      AzusatoException result = Assertions.assertThrows(AzusatoException.class, () -> {
        celeServiceAPI.getCelebrationContent(100000L, LOGIN_USER_NO, locale);
      });

      assertEquals(expect, result);

    }
  }

  @Nested
  class GetCelebrations {

    @Autowired
    ProfileProperty profileProperty;

    final String RESOUCE_PATH = RESOUCE_BASIC_PATH + "getCelebrations/";
    final long LOGIN_USER_NO = 1L;

    final int pageOfElement = 5;
    final int pagesOfpage = 3;
    final int currentPageNo = 1;

    /**
     * お祝いリストのorderbyテスト
     * 
     * @throws Exception
     */
    @Test
    public void When2data_givenNoAsc_resultOrderdbyNoDesc() throws Exception {
      String folderName = "1";
      dbUnitCompo
          .initalizeTable(Paths.get(RESOUCE_PATH, folderName, TestConstant.INIT_XML_FILE_NAME));
      GetCelebrationsSerivceAPIRequset req = GetCelebrationsSerivceAPIRequset.builder()
          .pageReq(MyPageControllerRequest.builder().currentPageNo(currentPageNo)
              .pagesOfpage(pagesOfpage).pageOfElement(pageOfElement).build())
          .build();

      GetCelebrationsSerivceAPIResponse response = celeServiceAPI.getCelebrations(req);



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

      assertEquals(expect, response);
    }

    @Test
    public void when2data_givenDeleted1data_result1Data() throws Exception {
      String folderName = "3";
      dbUnitCompo
          .initalizeTable(Paths.get(RESOUCE_PATH, folderName, TestConstant.INIT_XML_FILE_NAME));
      GetCelebrationsSerivceAPIRequset req = GetCelebrationsSerivceAPIRequset.builder()

          .pageReq(MyPageControllerRequest.builder().currentPageNo(currentPageNo)
              .pagesOfpage(pagesOfpage).pageOfElement(pageOfElement).build())
          .build();

      GetCelebrationsSerivceAPIResponse response = celeServiceAPI.getCelebrations(req);



      GetCelebrationsSerivceAPIResponse expect = GetCelebrationsSerivceAPIResponse.builder()
          .page(MyPageResponse.builder().currentPageNo(1).pages(List.of(1)).hasPrivious(false)
              .hasNext(false).totalPage(1).build())
          .celebrations(List.of(
              Celebration.builder().title(Entity.createdVarChars[0]).name(Entity.createdVarChars[2])
                  .profileImagePath(Entity.createdVarChars[0]).no(Entity.createdLongs[0])
                  .createdDatetime(Entity.createdDatetimes[0]).readCount(0).build()))
          .build();

      assertEquals(expect, response);
    }

    /**
     * 7個のデータがあってページングされたら、結局最後の二つを返す。
     * 
     * @throws Exception
     */
    @Test
    public void When7data_givenPagined_result2data() throws Exception {
      String folderName = "4";
      dbUnitCompo
          .initalizeTable(Paths.get(RESOUCE_PATH, folderName, TestConstant.INIT_XML_FILE_NAME));
      GetCelebrationsSerivceAPIRequset req = GetCelebrationsSerivceAPIRequset.builder()
          .pageReq(MyPageControllerRequest.builder().currentPageNo(2).pagesOfpage(pagesOfpage)
              .pageOfElement(pageOfElement).build())
          .build();

      GetCelebrationsSerivceAPIResponse response = celeServiceAPI.getCelebrations(req);



      GetCelebrationsSerivceAPIResponse expect = GetCelebrationsSerivceAPIResponse.builder()
          .page(MyPageResponse.builder().currentPageNo(2).pages(List.of(1, 2)).hasPrivious(false)
              .hasNext(false).totalPage(2).build())
          .celebrations(List.of(
              Celebration.builder().title(Entity.createdVarChars[2]).name(Entity.createdVarChars[2])
                  .profileImagePath(Entity.createdVarChars[0]).no(Entity.createdLongs[1])
                  .createdDatetime(Entity.createdDatetimes[1]).readCount(1).build(),
              Celebration.builder().title(Entity.createdVarChars[0]).name(Entity.createdVarChars[2])
                  .profileImagePath(Entity.createdVarChars[0]).no(Entity.createdLongs[0])
                  .createdDatetime(Entity.createdDatetimes[0]).readCount(0).build()))
          .build();

      assertEquals(expect, response);
    }

  }

  private void contentPathCheck() {
    String insertedPath = celeRepo.findAll().get(Entity.GET_INDEXS[0]).getContentPath();
    Assertions.assertNotNull(insertedPath);
    Assertions.assertTrue(Files.exists(Paths.get(celeProperty.getServerContentFolderPath())));
  }
}
