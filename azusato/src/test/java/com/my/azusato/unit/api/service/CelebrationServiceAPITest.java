package com.my.azusato.unit.api.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

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

import com.my.azusato.api.controller.MyPageControllerRequest;
import com.my.azusato.api.service.CelebrationServiceAPI;
import com.my.azusato.api.service.request.AddCelebrationServiceAPIRequest;
import com.my.azusato.api.service.request.GetCelebrationsSerivceAPIRequset;
import com.my.azusato.api.service.response.GetCelebrationsSerivceAPIResponse;
import com.my.azusato.api.service.response.GetCelebrationsSerivceAPIResponse.Celebration;
import com.my.azusato.api.service.response.GetCelebrationsSerivceAPIResponse.CelebrationReply;
import com.my.azusato.common.TestConstant;
import com.my.azusato.common.TestConstant.Entity;
import com.my.azusato.entity.UserEntity;
import com.my.azusato.exception.AzusatoException;
import com.my.azusato.integration.AbstractIntegration;
import com.my.azusato.page.MyPageResponse;
import com.my.azusato.property.ProfileProperty;

public class CelebrationServiceAPITest extends AbstractIntegration {

	@Autowired
	CelebrationServiceAPI celeServiceAPI;

	final String RESOUCE_BASIC_PATH = "src/test/data/unit/api/service/";

	@Nested
	class AddCelebration {

		final String RESOUCE_PATH = RESOUCE_BASIC_PATH + "addCelebration/";

		@Test
		public void admin_normal_case() throws Exception {
			String folderName = "1";
			String[] COMPARED_TABLE_NAME = { "user", "celebration" };

			dbUnitCompo.initalizeTable(Paths.get(RESOUCE_PATH, folderName, TestConstant.INIT_XML_FILE_NAME));
			celeServiceAPI.addCelebartionAdmin(getNormalReq(), TestConstant.LOCALE_JA);

			// compare tables
			for (String table : COMPARED_TABLE_NAME) {
				// exclude to compare dateTime columns when celebration table
				if (table.equals("celebration")) {
					dbUnitCompo.compareTable(Paths.get(RESOUCE_PATH, folderName, TestConstant.EXPECT_XML_FILE_NAME), table,
							TestConstant.DEFAULT_EXCLUDE_COLUMNS);
				} else if(table.equals("user") || table.equals("profile")) {
					dbUnitCompo.compareTable(Paths.get(RESOUCE_PATH, folderName, TestConstant.EXPECT_XML_FILE_NAME), table,
							TestConstant.DEFAULT_EXCLUDE_DATE_COLUMNS);
				} else {
					dbUnitCompo.compareTable(Paths.get(RESOUCE_PATH, folderName, TestConstant.EXPECT_XML_FILE_NAME), table);
				}
			}

		}

		@Test
		public void normal_case() throws Exception {
			String folderName = "2";
			String[] COMPARED_TABLE_NAME = { "user", "celebration", "celebration_notice" };

			dbUnitCompo.initalizeTable(Paths.get(RESOUCE_PATH, folderName, TestConstant.INIT_XML_FILE_NAME));
			celeServiceAPI.addCelebartion(getNormalReq(), TestConstant.LOCALE_JA);

			// compare tables
			for (String table : COMPARED_TABLE_NAME) {
				// exclude to compare dateTime columns when celebration table
				if (table.equals("celebration")) {
					dbUnitCompo.compareTable(Paths.get(RESOUCE_PATH, folderName, TestConstant.EXPECT_XML_FILE_NAME), table,
							TestConstant.DEFAULT_EXCLUDE_COLUMNS);
				} else if (table.equals("celebration_notice")) {
					dbUnitCompo.compareTable(Paths.get(RESOUCE_PATH, folderName, TestConstant.EXPECT_XML_FILE_NAME), table,
							new String[] { "celebration_no" });
				} else if(table.equals("user") || table.equals("profile")) {
					dbUnitCompo.compareTable(Paths.get(RESOUCE_PATH, folderName, TestConstant.EXPECT_XML_FILE_NAME), table,
							TestConstant.DEFAULT_EXCLUDE_DATE_COLUMNS);
				} else {
					dbUnitCompo.compareTable(Paths.get(RESOUCE_PATH, folderName, TestConstant.EXPECT_XML_FILE_NAME), table);
				}
			}

		}

		@Test
		public void notExistAdmin_normal_case() throws Exception {
			String folderName = "3";
			String[] COMPARED_TABLE_NAME = { "user", "celebration" };

			dbUnitCompo.initalizeTable(Paths.get(RESOUCE_PATH, folderName, TestConstant.INIT_XML_FILE_NAME));
			celeServiceAPI.addCelebartion(getNormalReq(), TestConstant.LOCALE_JA);

			// compare tables
			for (String table : COMPARED_TABLE_NAME) {
				// exclude to compare dateTime columns when celebration table
				if (table.equals("celebration")) {
					dbUnitCompo.compareTable(Paths.get(RESOUCE_PATH, folderName, TestConstant.EXPECT_XML_FILE_NAME), table,
							TestConstant.DEFAULT_EXCLUDE_COLUMNS);
				} else if(table.equals("user") || table.equals("profile")) {
					dbUnitCompo.compareTable(Paths.get(RESOUCE_PATH, folderName, TestConstant.EXPECT_XML_FILE_NAME), table,
							TestConstant.DEFAULT_EXCLUDE_DATE_COLUMNS);
				} else {
					dbUnitCompo.compareTable(Paths.get(RESOUCE_PATH, folderName, TestConstant.EXPECT_XML_FILE_NAME), table);
				}
			}

			Assertions.assertEquals(0, celeNoticeRepo.count());

		}

		@ParameterizedTest
		@MethodSource("com.my.azusato.common.TestSource#locales")
		public void notExistUser_abnormal_case(Locale locale) throws Exception {
			AddCelebrationServiceAPIRequest req = new AddCelebrationServiceAPIRequest();
			req.setUserNo(100000L);

			String tableName = messageSource.getMessage(UserEntity.TABLE_NAME_KEY, null, locale);
			AzusatoException expect = new AzusatoException(HttpStatus.INTERNAL_SERVER_ERROR, AzusatoException.E0001,
					messageSource.getMessage(AzusatoException.E0001, new String[] { tableName }, locale));

			AzusatoException result = Assertions.assertThrows(AzusatoException.class, () -> {
				celeServiceAPI.addCelebartion(req, locale);
			});

			assertEquals(expect, result);

		}

		private AddCelebrationServiceAPIRequest getNormalReq() {
			return AddCelebrationServiceAPIRequest.builder()
					.name(Entity.updatedVarChars[0]).profileImageBase64(Entity.updatedVarChars[1]).profileImageType(Entity.updatedVarChars[2])
					.title(Entity.createdVarChars[0]).content(Entity.createdVarChars[1]).userNo(Long.valueOf(Entity.createdInts[0]))
					.build();
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
		 * お祝いリスト、お祝い書き込みリストのorderbyテスト
		 * @throws Exception
		 */
		@Test
		public void When2data_givenNoAsc_resultOrderdbyNoDesc() throws Exception {
			String folderName = "1";
			dbUnitCompo.initalizeTable(Paths.get(RESOUCE_PATH, folderName, TestConstant.INIT_XML_FILE_NAME));
			GetCelebrationsSerivceAPIRequset req = GetCelebrationsSerivceAPIRequset.builder()
									.loginUserNo(LOGIN_USER_NO)
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
							.content(Entity.createdVarChars[3])
							.name(Entity.createdVarChars[2])
							.profileImageType(Entity.ImageType[0])
							.profileImageBase64(Entity.createdVarChars[0])
							.no(Entity.createdLongs[1])
							.owner(true)
							.replys(List.of())
							.createdDatetime(Entity.createdDatetimes[1])
							.build(),
							Celebration.builder()
								.title(Entity.createdVarChars[0])
								.content(Entity.createdVarChars[1])
								.name(Entity.createdVarChars[2])
								.profileImageType(Entity.ImageType[0])
								.profileImageBase64(Entity.createdVarChars[0])
								.no(Entity.createdLongs[0])
								.owner(true)
								.replys(List.of(
										CelebrationReply.builder()
											.no(Entity.createdLongs[0])
											.content(Entity.createdVarChars[0])
											.createdDatetime(Entity.createdDatetimes[0])
											.owner(true).build(),
										CelebrationReply.builder()
											.no(Entity.createdLongs[1])
											.content(Entity.createdVarChars[1])
											.createdDatetime(Entity.createdDatetimes[1])
											.owner(true).build()
										))
								.createdDatetime(Entity.createdDatetimes[0])
								.build()
							))		
					.build();
			
			assertEquals(expect, response);
		}
		
		/**
		 * ユーザが二人の場合、ownerがちゃんと合っているか確認
		 * @throws Exception
		 */
		@Test
		public void When2DataAnd2User_given2User_resultOwner() throws Exception {
			String folderName = "2";
			dbUnitCompo.initalizeTable(Paths.get(RESOUCE_PATH, folderName, TestConstant.INIT_XML_FILE_NAME));
			GetCelebrationsSerivceAPIRequset req = GetCelebrationsSerivceAPIRequset.builder()
									.loginUserNo(LOGIN_USER_NO)
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
							.content(Entity.createdVarChars[3])
							.name(Entity.createdVarChars[2])
							.profileImageType(Entity.ImageType[0])
							.profileImageBase64(Entity.createdVarChars[0])
							.no(Entity.createdLongs[1])
							.owner(false)
							.replys(List.of())
							.createdDatetime(Entity.createdDatetimes[1])
							.build(),
							Celebration.builder()
								.title(Entity.createdVarChars[0])
								.content(Entity.createdVarChars[1])
								.name(Entity.createdVarChars[2])
								.profileImageType(Entity.ImageType[0])
								.profileImageBase64(Entity.createdVarChars[0])
								.no(Entity.createdLongs[0])
								.owner(true)
								.replys(List.of(
										CelebrationReply.builder()
											.no(Entity.createdLongs[0])
											.content(Entity.createdVarChars[0])
											.createdDatetime(Entity.createdDatetimes[0])
											.owner(true).build(),
										CelebrationReply.builder()
											.no(Entity.createdLongs[1])
											.content(Entity.createdVarChars[1])
											.createdDatetime(Entity.createdDatetimes[1])
											.owner(false).build()
										))
								.createdDatetime(Entity.createdDatetimes[0])
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
									.loginUserNo(LOGIN_USER_NO)
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
								.content(Entity.createdVarChars[1])
								.name(Entity.createdVarChars[2])
								.profileImageType(Entity.ImageType[0])
								.profileImageBase64(Entity.createdVarChars[0])
								.no(Entity.createdLongs[0])
								.owner(true)
								.replys(List.of(
										CelebrationReply.builder()
											.no(Entity.createdLongs[0])
											.content(Entity.createdVarChars[0])
											.createdDatetime(Entity.createdDatetimes[0])
											.owner(true).build()
										))
								.createdDatetime(Entity.createdDatetimes[0])
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
									.loginUserNo(LOGIN_USER_NO)
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
							.content(Entity.createdVarChars[3])
							.name(Entity.createdVarChars[2])
							.profileImageType(Entity.ImageType[0])
							.profileImageBase64(Entity.createdVarChars[0])
							.no(Entity.createdLongs[1])
							.owner(true)
							.replys(List.of())
							.createdDatetime(Entity.createdDatetimes[1])
							.build(),
							Celebration.builder()
								.title(Entity.createdVarChars[0])
								.content(Entity.createdVarChars[1])
								.name(Entity.createdVarChars[2])
								.profileImageType(Entity.ImageType[0])
								.profileImageBase64(Entity.createdVarChars[0])
								.no(Entity.createdLongs[0])
								.owner(true)
								.replys(List.of(
										CelebrationReply.builder()
											.no(Entity.createdLongs[0])
											.content(Entity.createdVarChars[0])
											.createdDatetime(Entity.createdDatetimes[0])
											.owner(true).build(),
										CelebrationReply.builder()
											.no(Entity.createdLongs[1])
											.content(Entity.createdVarChars[1])
											.createdDatetime(Entity.createdDatetimes[1])
											.owner(true).build()
										))
								.createdDatetime(Entity.createdDatetimes[0])
								.build()
							))		
					.build();
			
			assertEquals(expect, response);
		}
		
	}
}
