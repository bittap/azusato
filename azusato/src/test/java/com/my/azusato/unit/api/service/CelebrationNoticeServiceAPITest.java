package com.my.azusato.unit.api.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import com.my.azusato.api.controller.request.MyPageControllerRequest;
import com.my.azusato.api.service.CelebrationNoticeServiceAPI;
import com.my.azusato.api.service.request.GetCelebrationsSerivceAPIRequset;
import com.my.azusato.api.service.response.GetCelebrationsSerivceAPIResponse;
import com.my.azusato.api.service.response.GetCelebrationsSerivceAPIResponse.Celebration;
import com.my.azusato.common.TestConstant;
import com.my.azusato.common.TestConstant.Entity;
import com.my.azusato.exception.AzusatoException;
import com.my.azusato.integration.AbstractIntegration;
import com.my.azusato.page.MyPageResponse;
import com.my.azusato.property.ProfileProperty;

public class CelebrationNoticeServiceAPITest extends AbstractIntegration {

	@Autowired
	CelebrationNoticeServiceAPI target;

	final String RESOUCE_BASIC_PATH = "src/test/data/unit/api/service/CelebrationNoticeServiceAPI/";
	
	final String CELEBRATION_NOTICE_TABLE_NAME = "celebration_notice";

	@Nested
	class read {

		final String RESOUCE_PATH = RESOUCE_BASIC_PATH + "read/";
		
		final long CELEBRATION_NO = 1L;
		
		final long LOGIN_USER_NO = 1L;

		@Test
		@DisplayName("正常系_更新成功")
		void givenNormal_resultUpdated() throws Exception {
			String folderName = "1";
			dbUnitCompo.initalizeTable(Paths.get(RESOUCE_PATH, folderName, TestConstant.INIT_XML_FILE_NAME));
			
			target.read(CELEBRATION_NO, LOGIN_USER_NO, null);
			
			dbUnitCompo.compareTable(Paths.get(RESOUCE_PATH, folderName, TestConstant.EXPECT_XML_FILE_NAME), CELEBRATION_NOTICE_TABLE_NAME,
					new String[]{ "update_datetime" ,"read_datetime"});
		}

		@DisplayName("準正常系_お祝い通知が存在しない_結果例外スロー")
		@ParameterizedTest
		@MethodSource("com.my.azusato.unit.api.service.CelebrationNoticeServiceAPITest#givenNoExistCelebrationNotice_resultThrow")
		public void givenNoExistCelebrationNotice_resultThrow(Locale locale,String expectMessage) throws Exception {
		
			AzusatoException expect = new AzusatoException(HttpStatus.BAD_REQUEST, "I-0005",expectMessage);

			AzusatoException result = Assertions.assertThrows(AzusatoException.class, () -> {
				target.read(CELEBRATION_NO, LOGIN_USER_NO, locale);
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
		 * @throws Exception
		 */
		@Test
		public void When2data_givenNoAsc_resultOrderdbyNoDesc() throws Exception {
			String folderName = "1";
			dbUnitCompo.initalizeTable(Paths.get(RESOUCE_PATH, folderName, TestConstant.INIT_XML_FILE_NAME));
			GetCelebrationsSerivceAPIRequset req = GetCelebrationsSerivceAPIRequset.builder()
									.pageReq(MyPageControllerRequest.builder()
											.currentPageNo(currentPageNo).pagesOfpage(pagesOfpage).pageOfElement(pageOfElement)
											.build())
									.build();
										
			GetCelebrationsSerivceAPIResponse response  = celeServiceAPI.getCelebrations(req);
			
			
			
			GetCelebrationsSerivceAPIResponse expect = GetCelebrationsSerivceAPIResponse.builder()
					.page(MyPageResponse.builder().currentPageNo(1).pages(List.of(1)).hasPrivious(false).hasNext(false).totalPage(1).build())
					.celebrations(List.of(
							Celebration.builder()
								.title(Entity.createdVarChars[2])
								.name(Entity.createdVarChars[2])
								.profileImagePath(Entity.createdVarChars[0])
								.no(Entity.createdLongs[1])
								.createdDatetime(Entity.createdDatetimes[1])
								.readCount(1)
								.build(),
							Celebration.builder()
								.title(Entity.createdVarChars[0])
								.name(Entity.createdVarChars[2])
								.profileImagePath(Entity.createdVarChars[0])
								.no(Entity.createdLongs[0])
								.createdDatetime(Entity.createdDatetimes[0])
								.readCount(0)
								.build()
							))		
					.build();
			
			assertEquals(expect, response);
		}
		
		@Test
		public void when2data_givenDeleted1data_result1Data() throws Exception {
			String folderName = "3";
			dbUnitCompo.initalizeTable(Paths.get(RESOUCE_PATH, folderName, TestConstant.INIT_XML_FILE_NAME));
			GetCelebrationsSerivceAPIRequset req = GetCelebrationsSerivceAPIRequset.builder()

									.pageReq(MyPageControllerRequest.builder()
											.currentPageNo(currentPageNo).pagesOfpage(pagesOfpage).pageOfElement(pageOfElement)
											.build())
									.build();
										
			GetCelebrationsSerivceAPIResponse response  = celeServiceAPI.getCelebrations(req);
			
			
			
			GetCelebrationsSerivceAPIResponse expect = GetCelebrationsSerivceAPIResponse.builder()
					.page(MyPageResponse.builder().currentPageNo(1).pages(List.of(1)).hasPrivious(false).hasNext(false).totalPage(1).build())
					.celebrations(List.of(
							Celebration.builder()
								.title(Entity.createdVarChars[0])
								.name(Entity.createdVarChars[2])
								.profileImagePath(Entity.createdVarChars[0])
								.no(Entity.createdLongs[0])
								.createdDatetime(Entity.createdDatetimes[0])
								.readCount(0)
								.build()
							))		
					.build();
			
			assertEquals(expect, response);
		}
		
		/**
		 * 7個のデータがあってページングされたら、結局最後の二つを返す。
		 * @throws Exception
		 */
		@Test
		public void When7data_givenPagined_result2data() throws Exception {
			String folderName = "4";
			dbUnitCompo.initalizeTable(Paths.get(RESOUCE_PATH, folderName, TestConstant.INIT_XML_FILE_NAME));
			GetCelebrationsSerivceAPIRequset req = GetCelebrationsSerivceAPIRequset.builder()
									.pageReq(MyPageControllerRequest.builder()
											.currentPageNo(2).pagesOfpage(pagesOfpage).pageOfElement(pageOfElement)
											.build())
									.build();
										
			GetCelebrationsSerivceAPIResponse response  = celeServiceAPI.getCelebrations(req);
			
			
			
			GetCelebrationsSerivceAPIResponse expect = GetCelebrationsSerivceAPIResponse.builder()
					.page(MyPageResponse.builder().currentPageNo(2).pages(List.of(1,2)).hasPrivious(false).hasNext(false).totalPage(2).build())
					.celebrations(List.of(
							Celebration.builder()
								.title(Entity.createdVarChars[2])
								.name(Entity.createdVarChars[2])
								.profileImagePath(Entity.createdVarChars[0])
								.no(Entity.createdLongs[1])
								.createdDatetime(Entity.createdDatetimes[1])
								.readCount(1)
								.build(),
							Celebration.builder()
								.title(Entity.createdVarChars[0])
								.name(Entity.createdVarChars[2])
								.profileImagePath(Entity.createdVarChars[0])
								.no(Entity.createdLongs[0])
								.createdDatetime(Entity.createdDatetimes[0])
								.readCount(0)
								.build()
							))		
					.build();
			
			assertEquals(expect, response);
		}
		
	}
	
	static Stream<Arguments> givenNoExistCelebrationNotice_resultThrow(){
		return Stream.of(
				Arguments.of(TestConstant.LOCALE_JA,"お祝い通知情報が存在しないです。"),
				Arguments.of(TestConstant.LOCALE_KO,"축하알람정보가 존재하지 않습니다.")
		);
	}
	
	private void contentPathCheck() {
		String insertedPath = celeRepo.findAll().get(Entity.GET_INDEXS[0]).getContentPath();
		Assertions.assertNotNull(insertedPath);
		Assertions.assertTrue(Files.exists(Paths.get(celeProperty.getServerContentFolderPath())));
	}
}
