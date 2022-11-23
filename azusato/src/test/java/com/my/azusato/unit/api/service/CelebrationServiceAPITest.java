package com.my.azusato.unit.api.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.nio.file.Paths;
import java.util.List;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import com.my.azusato.api.controller.request.MyPageControllerRequest;
import com.my.azusato.api.service.CelebrationServiceAPI;
import com.my.azusato.api.service.request.GetCelebrationsSerivceAPIRequset;
import com.my.azusato.api.service.response.GetCelebrationsSerivceAPIResponse;
import com.my.azusato.api.service.response.GetCelebrationsSerivceAPIResponse.Celebration;
import com.my.azusato.common.TestConstant;
import com.my.azusato.common.TestConstant.Entity;
import com.my.azusato.integration.AbstractIntegration;
import com.my.azusato.page.MyPageResponse;
import com.my.azusato.property.ProfileProperty;

public class CelebrationServiceAPITest extends AbstractIntegration {

  @Autowired
  CelebrationServiceAPI celeServiceAPI;

  final String RESOUCE_BASIC_PATH = "src/test/data/unit/api/service/";

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
}
