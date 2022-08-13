package com.my.azusato.unit.api.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Paths;
import java.time.LocalDateTime;
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
import com.my.azusato.api.service.response.GetCelebrationNoticesSerivceAPIResponse;
import com.my.azusato.api.service.response.GetCelebrationNoticesSerivceAPIResponse.Notice;
import com.my.azusato.common.TestConstant;
import com.my.azusato.common.TestConstant.Entity;
import com.my.azusato.exception.AzusatoException;
import com.my.azusato.integration.AbstractIntegration;

public class CelebrationNoticeServiceAPITest extends AbstractIntegration {

	@Autowired
	CelebrationNoticeServiceAPI target;

	final String RESOUCE_BASIC_PATH = "src/test/data/unit/api/service/CelebrationNoticeServiceAPI/";
	
	final String CELEBRATION_NOTICE_TABLE_NAME = "celebration_notice";

	@Nested
	@DisplayName("お祝い通知の既読処理")
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
	@DisplayName("お祝い通知情報リストの返却")
	class celebrationNotices {
		
		final String RESOUCE_PATH = RESOUCE_BASIC_PATH + "celebrationNotices/";
		final long LOGIN_USER_NO = 1L;
		
		final int pageOfElement = 5;
		final int currentPageNo = 1;
		
		@Test
		@DisplayName("正常系_2個のデータがある場合_2個のデータを返す")
		public void given2data_resultReturn2Data() throws Exception {
			/*
			 * 準備
			 */
			String folderName = "1";
			dbUnitCompo.initalizeTable(Paths.get(RESOUCE_PATH, folderName, TestConstant.INIT_XML_FILE_NAME));
			
			GetCelebrationsSerivceAPIRequset req = GetCelebrationsSerivceAPIRequset.builder()
									.pageReq(MyPageControllerRequest.builder().currentPageNo(currentPageNo).pageOfElement(pageOfElement).build())
									.build();
			
			/*
			 * 予測結果
			 */
			
			GetCelebrationNoticesSerivceAPIResponse expect = GetCelebrationNoticesSerivceAPIResponse.builder()
					.notices(List.of(
							Notice.builder()
								.name(Entity.createdVarChars[5])
								.title(Entity.createdVarChars[1])
								.createdDatetime(Entity.createdDatetimes[1])
								.celebrationNo(Entity.createdLongs[0])
								.celebrationReplyNo(Entity.createdLongs[0])
								.profileImagePath(Entity.createdVarChars[1])
								.readed(false)
								.build(),
							Notice.builder()
								.name(Entity.createdVarChars[2])
								.title(Entity.createdVarChars[0])
								.createdDatetime(Entity.createdDatetimes[0])
								.celebrationNo(Entity.createdLongs[0])
								.celebrationReplyNo(null)
								.profileImagePath(Entity.createdVarChars[0])
								.readed(false)
								.build()
							))		
					.noReadLength(2)
							.build();
			
			/*
			 * 実行
			 */
			GetCelebrationNoticesSerivceAPIResponse result = target.celebrationNotices(req, LOGIN_USER_NO);
			
			
			assertEquals(expect, result);
		}
		
		@Test
		@DisplayName("正常系_0個のデータがある場合_0個のデータを返す")
		public void given0data_resultReturn0Data() throws Exception {
			/*
			 * 準備
			 */	
			GetCelebrationsSerivceAPIRequset req = GetCelebrationsSerivceAPIRequset.builder()
									.pageReq(MyPageControllerRequest.builder().currentPageNo(currentPageNo).pageOfElement(pageOfElement).build())
									.build();
			
			/*
			 * 予測結果
			 */
			
			GetCelebrationNoticesSerivceAPIResponse expect = GetCelebrationNoticesSerivceAPIResponse.builder()
					.notices(List.of())		
					.noReadLength(0)
							.build();
			
			/*
			 * 実行
			 */
			GetCelebrationNoticesSerivceAPIResponse result = target.celebrationNotices(req, LOGIN_USER_NO);
			
			
			assertEquals(expect, result);
		}
		
		@Test
		@DisplayName("正常系(ソート確認)_2個のデータがある場合_参照フラグ昇順の2個のデータを返す")
		public void given2data_resultReturnReadedAsc2Data() throws Exception {
			/*
			 * 準備
			 */
			String folderName = "2";
			dbUnitCompo.initalizeTable(Paths.get(RESOUCE_PATH, folderName, TestConstant.INIT_XML_FILE_NAME));
			
			GetCelebrationsSerivceAPIRequset req = GetCelebrationsSerivceAPIRequset.builder()
									.pageReq(MyPageControllerRequest.builder().currentPageNo(currentPageNo).pageOfElement(pageOfElement).build())
									.build();
			
			/*
			 * 予測結果
			 */
			
			GetCelebrationNoticesSerivceAPIResponse expect = GetCelebrationNoticesSerivceAPIResponse.builder()
					.notices(List.of(
							Notice.builder()
								.name(Entity.createdVarChars[2])
								.title(Entity.createdVarChars[0])
								.createdDatetime(Entity.createdDatetimes[0])
								.celebrationNo(Entity.createdLongs[0])
								.celebrationReplyNo(null)
								.profileImagePath(Entity.createdVarChars[0])
								.readed(false)
								.build(),
							Notice.builder()
								.name(Entity.createdVarChars[2])
								.title(Entity.createdVarChars[1])
								.createdDatetime(Entity.createdDatetimes[1])
								.celebrationNo(Entity.createdLongs[1])
								.celebrationReplyNo(null)
								.profileImagePath(Entity.createdVarChars[0])
								.readed(true)
								.build()
							))		
					.noReadLength(1)
							.build();
			
			/*
			 * 実行
			 */
			GetCelebrationNoticesSerivceAPIResponse result = target.celebrationNotices(req, LOGIN_USER_NO);
			
			
			assertEquals(expect, result);
		}
		
		@Test
		@DisplayName("正常系(ソート確認)_2個のデータがある場合_お祝い番号降順の2個のデータを返す")
		public void given2data_resultReturnCelebrationNoDesc2Data() throws Exception {
			/*
			 * 準備
			 */
			String folderName = "3";
			dbUnitCompo.initalizeTable(Paths.get(RESOUCE_PATH, folderName, TestConstant.INIT_XML_FILE_NAME));
			
			GetCelebrationsSerivceAPIRequset req = GetCelebrationsSerivceAPIRequset.builder()
									.pageReq(MyPageControllerRequest.builder().currentPageNo(currentPageNo).pageOfElement(pageOfElement).build())
									.build();
			
			/*
			 * 予測結果
			 */
			
			GetCelebrationNoticesSerivceAPIResponse expect = GetCelebrationNoticesSerivceAPIResponse.builder()
					.notices(List.of(
							Notice.builder()
							.name(Entity.createdVarChars[2])
							.title(Entity.createdVarChars[1])
							.createdDatetime(Entity.createdDatetimes[1])
							.celebrationNo(Entity.createdLongs[1])
							.celebrationReplyNo(null)
							.profileImagePath(Entity.createdVarChars[0])
							.readed(false)
							.build(),
							Notice.builder()
								.name(Entity.createdVarChars[2])
								.title(Entity.createdVarChars[0])
								.createdDatetime(Entity.createdDatetimes[0])
								.celebrationNo(Entity.createdLongs[0])
								.celebrationReplyNo(null)
								.profileImagePath(Entity.createdVarChars[0])
								.readed(false)
								.build()
							))		
					.noReadLength(2)
							.build();
			
			/*
			 * 実行
			 */
			GetCelebrationNoticesSerivceAPIResponse result = target.celebrationNotices(req, LOGIN_USER_NO);
			
			
			assertEquals(expect, result);
		}
		
		@Test
		@DisplayName("正常系(ソート確認)_2個のデータがある場合_お祝い書き込み番号降順の2個のデータを返す")
		public void given2data_resultReturnCelebrationReplyNoDesc2Data() throws Exception {
			/*
			 * 準備
			 */
			String folderName = "4";
			dbUnitCompo.initalizeTable(Paths.get(RESOUCE_PATH, folderName, TestConstant.INIT_XML_FILE_NAME));
			
			GetCelebrationsSerivceAPIRequset req = GetCelebrationsSerivceAPIRequset.builder()
									.pageReq(MyPageControllerRequest.builder().currentPageNo(currentPageNo).pageOfElement(pageOfElement).build())
									.build();
			
			/*
			 * 予測結果
			 */
			
			GetCelebrationNoticesSerivceAPIResponse expect = GetCelebrationNoticesSerivceAPIResponse.builder()
					.notices(List.of(
							Notice.builder()
								.name(Entity.createdVarChars[2])
								.title(Entity.createdVarChars[1])
								.createdDatetime(Entity.createdDatetimes[1])
								.celebrationNo(Entity.createdLongs[0])
								.celebrationReplyNo(Entity.createdLongs[1])
								.profileImagePath(Entity.createdVarChars[0])
								.readed(false)
								.build(),
							Notice.builder()
								.name(Entity.createdVarChars[2])
								.title(Entity.createdVarChars[0])
								.createdDatetime(Entity.createdDatetimes[0])
								.celebrationNo(Entity.createdLongs[0])
								.celebrationReplyNo(Entity.createdLongs[0])
								.profileImagePath(Entity.createdVarChars[0])
								.readed(false)
								.build()
							))		
					.noReadLength(2)
							.build();
			
			/*
			 * 実行
			 */
			GetCelebrationNoticesSerivceAPIResponse result = target.celebrationNotices(req, LOGIN_USER_NO);
			
			
			assertEquals(expect, result);
		}
		
		@Test
		@DisplayName("正常系(ソート確認)_4個のデータがある場合_お祝い書き込み番号降順の4個のデータを返す")
		public void given4data_resultReturnReadedAscCelebrationNoDescCelebrationReplyNoDesc2Data() throws Exception {
			/*
			 * 準備
			 */
			String folderName = "5";
			dbUnitCompo.initalizeTable(Paths.get(RESOUCE_PATH, folderName, TestConstant.INIT_XML_FILE_NAME));
			
			GetCelebrationsSerivceAPIRequset req = GetCelebrationsSerivceAPIRequset.builder()
									.pageReq(MyPageControllerRequest.builder().currentPageNo(currentPageNo).pageOfElement(pageOfElement).build())
									.build();
			
			/*
			 * 予測結果
			 */
			
			GetCelebrationNoticesSerivceAPIResponse expect = GetCelebrationNoticesSerivceAPIResponse.builder()
					.notices(List.of(
							Notice.builder()
								.name(Entity.createdVarChars[2])
								.title("1番目")
								.createdDatetime(LocalDateTime.of(2022, 1, 1, 1, 1, 1))
								.celebrationNo(2L)
								.celebrationReplyNo(2L)
								.profileImagePath(Entity.createdVarChars[0])
								.readed(false)
								.build(),
							Notice.builder()
								.name(Entity.createdVarChars[2])
								.title("2番目")
								.createdDatetime(LocalDateTime.of(2022, 2, 2, 2, 2, 2))
								.celebrationNo(2L)
								.celebrationReplyNo(1L)
								.profileImagePath(Entity.createdVarChars[0])
								.readed(false)
								.build(),
							Notice.builder()
								.name(Entity.createdVarChars[2])
								.title("3番目")
								.createdDatetime(LocalDateTime.of(2022, 3, 3, 3, 3, 3))
								.celebrationNo(1L)
								.celebrationReplyNo(null)
								.profileImagePath(Entity.createdVarChars[0])
								.readed(false)
								.build(),
							Notice.builder()
								.name(Entity.createdVarChars[2])
								.title("4番目")
								.createdDatetime(LocalDateTime.of(2022, 4, 4, 4, 4, 4))
								.celebrationNo(3L)
								.celebrationReplyNo(null)
								.profileImagePath(Entity.createdVarChars[0])
								.readed(true)
								.build()
							))		
					.noReadLength(3)
							.build();
			
			/*
			 * 実行
			 */
			GetCelebrationNoticesSerivceAPIResponse result = target.celebrationNotices(req, LOGIN_USER_NO);
			
			
			assertEquals(expect, result);
		}
		
		@Test
		@DisplayName("正常系(ページング確認)_7個のデータがある場合_5個のデータを返す")
		public void given7data_resultReturn5Data() throws Exception {
			/*
			 * 準備
			 */
			String folderName = "6";
			dbUnitCompo.initalizeTable(Paths.get(RESOUCE_PATH, folderName, TestConstant.INIT_XML_FILE_NAME));
			
			GetCelebrationsSerivceAPIRequset req = GetCelebrationsSerivceAPIRequset.builder()
									.pageReq(MyPageControllerRequest.builder().currentPageNo(currentPageNo).pageOfElement(pageOfElement).build())
									.build();
			
			/*
			 * 予測結果
			 */
			
			GetCelebrationNoticesSerivceAPIResponse expect = GetCelebrationNoticesSerivceAPIResponse.builder()
					.notices(List.of(
							Notice.builder()
								.name(Entity.createdVarChars[2])
								.title(Entity.createdVarChars[0])
								.createdDatetime(Entity.createdDatetime)
								.celebrationNo(7L)
								.profileImagePath(Entity.createdVarChars[0])
								.readed(false)
								.build(),
								Notice.builder()
								.name(Entity.createdVarChars[2])
								.title(Entity.createdVarChars[0])
								.createdDatetime(Entity.createdDatetime)
								.celebrationNo(6L)
								.profileImagePath(Entity.createdVarChars[0])
								.readed(false)
								.build(),
								Notice.builder()
								.name(Entity.createdVarChars[2])
								.title(Entity.createdVarChars[0])
								.createdDatetime(Entity.createdDatetime)
								.celebrationNo(5L)
								.profileImagePath(Entity.createdVarChars[0])
								.readed(false)
								.build(),
								Notice.builder()
								.name(Entity.createdVarChars[2])
								.title(Entity.createdVarChars[0])
								.createdDatetime(Entity.createdDatetime)
								.celebrationNo(4L)
								.profileImagePath(Entity.createdVarChars[0])
								.readed(false)
								.build(),
								Notice.builder()
								.name(Entity.createdVarChars[2])
								.title(Entity.createdVarChars[0])
								.createdDatetime(Entity.createdDatetime)
								.celebrationNo(3L)
								.profileImagePath(Entity.createdVarChars[0])
								.readed(false)
								.build()
							))		
					.noReadLength(5)
							.build();
			
			/*
			 * 実行
			 */
			GetCelebrationNoticesSerivceAPIResponse result = target.celebrationNotices(req, LOGIN_USER_NO);
			
			
			assertEquals(expect, result);
		}
		
		
	}
	
	static Stream<Arguments> givenNoExistCelebrationNotice_resultThrow(){
		return Stream.of(
				Arguments.of(TestConstant.LOCALE_JA,"お祝い通知情報が存在しないです。"),
				Arguments.of(TestConstant.LOCALE_KO,"축하알람정보가 존재하지 않습니다.")
		);
	}
}
