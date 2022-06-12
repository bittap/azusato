package com.my.azusato.unit.api.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Paths;
import java.util.Locale;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import com.my.azusato.api.controller.request.AddCelebrationReplyAPIReqeust;
import com.my.azusato.api.service.CelebrationReplyServiceAPI;
import com.my.azusato.common.TestConstant;
import com.my.azusato.common.TestConstant.Entity;
import com.my.azusato.entity.CelebrationContentEntity;
import com.my.azusato.entity.CelebrationReplyEntity;
import com.my.azusato.entity.UserEntity;
import com.my.azusato.exception.AzusatoException;
import com.my.azusato.integration.AbstractIntegration;

public class CelebrationReplyServiceAPITest extends AbstractIntegration {

	@Autowired
	CelebrationReplyServiceAPI celeReplyServiceAPI;
	


	final String RESOUCE_BASIC_PATH = "src/test/data/unit/api/service/";

	@Nested
	class AddCelebrationReply {

		final String RESOUCE_PATH = RESOUCE_BASIC_PATH + "addCelebration-reply/";
		
		final long CELEBRATION_NO = 1L;
		final long LOGIN_USER_NO = 1L;

		/**
		 * 書き込み作成者が本人で書き込みがない場合は、結果書き込み「一つ」と「通知」はゼロ
		 * @throws Exception
		 */
		@Test
		public void givenWriteSelfAndNoReply_resultReply1AndNoticeZero() throws Exception {
			String folderName = "1";
			String[] COMPARED_TABLE_NAME = { "user", "profile","celebration", "celebration_reply" };

			dbUnitCompo.initalizeTable(Paths.get(RESOUCE_PATH, folderName, TestConstant.INIT_XML_FILE_NAME));
			celeReplyServiceAPI.addCelebartionReply(getNormalReq(),CELEBRATION_NO,LOGIN_USER_NO,TestConstant.LOCALE_JA);

			// compare tables
			for (String table : COMPARED_TABLE_NAME) {
				// exclude to compare dateTime columns when celebration table
				if (table.equals("celebration_reply")) {
					dbUnitCompo.compareTable(Paths.get(RESOUCE_PATH, folderName, TestConstant.EXPECT_XML_FILE_NAME), table,
							TestConstant.DEFAULT_EXCLUDE_COLUMNS);
				} else if(table.equals("user")) {
					dbUnitCompo.compareTable(Paths.get(RESOUCE_PATH, folderName, TestConstant.EXPECT_XML_FILE_NAME), table,
							TestConstant.DEFAULT_EXCLUDE_DATE_COLUMNS);
				} else {
					dbUnitCompo.compareTable(Paths.get(RESOUCE_PATH, folderName, TestConstant.EXPECT_XML_FILE_NAME), table);
				}
			}
			
			assertEquals(0, celeReplyNoticeRepo.findAll().size());
		}
		
		@ParameterizedTest
		@MethodSource("com.my.azusato.common.TestSource#locales")
		public void notExistReply_abnormal_case(Locale locale) throws Exception {
			String folderName = "2";

			dbUnitCompo.initalizeTable(Paths.get(RESOUCE_PATH, folderName, TestConstant.INIT_XML_FILE_NAME));
			
			String tableName = messageSource.getMessage(CelebrationContentEntity.TABLE_NAME_KEY, null, locale);
			AzusatoException expect = new AzusatoException(HttpStatus.BAD_REQUEST, AzusatoException.I0005,
					messageSource.getMessage(AzusatoException.I0005, new String[] { tableName }, locale));

			AzusatoException result = Assertions.assertThrows(AzusatoException.class, () -> {
				celeReplyServiceAPI.addCelebartionReply(null, 100000L, LOGIN_USER_NO ,locale);
			});

			assertEquals(expect, result);

		}
		
		/**
		 * 書き込み作成者が本人で書き込みがある場合は、結果「書き込み」と「通知」が正常
		 * @throws Exception
		 */
		@Test
		public void givenWriteSelfAndExistReply_result200() throws Exception {
			String folderName = "3";
			String[] COMPARED_TABLE_NAME = { "user", "profile","celebration", "celebration_reply" , "celebration_reply_notice" };

			dbUnitCompo.initalizeTable(Paths.get(RESOUCE_PATH, folderName, TestConstant.INIT_XML_FILE_NAME));
			celeReplyServiceAPI.addCelebartionReply(getNormalReq(),CELEBRATION_NO,LOGIN_USER_NO,TestConstant.LOCALE_JA);

			// compare tables
			for (String table : COMPARED_TABLE_NAME) {
				// exclude to compare dateTime columns when celebration table
				if (table.equals("celebration_reply")) {
					dbUnitCompo.compareTable(Paths.get(RESOUCE_PATH, folderName, TestConstant.EXPECT_XML_FILE_NAME), table,
							TestConstant.DEFAULT_EXCLUDE_COLUMNS);
				} else if (table.equals("celebration_reply_notice")) {
					dbUnitCompo.compareTable(Paths.get(RESOUCE_PATH, folderName, TestConstant.EXPECT_XML_FILE_NAME), table,
							new String[] { "celebration_reply_no" });
				} else if(table.equals("user")) {
					dbUnitCompo.compareTable(Paths.get(RESOUCE_PATH, folderName, TestConstant.EXPECT_XML_FILE_NAME), table,
							TestConstant.DEFAULT_EXCLUDE_DATE_COLUMNS);
				} else {
					dbUnitCompo.compareTable(Paths.get(RESOUCE_PATH, folderName, TestConstant.EXPECT_XML_FILE_NAME), table);
				}
			}
			
			assertEquals(2, celeReplyNoticeRepo.findAll().size());
		}
		
		/**
		 * 書き込み作成者が非本人で書き込みがある場合は、結果「書き込み」と「通知」が正常
		 * @throws Exception
		 */
		@Test
		public void givenWriteAndExistReply_result200() throws Exception {
			String folderName = "4";
			String[] COMPARED_TABLE_NAME = { "user", "profile","celebration", "celebration_reply" , "celebration_reply_notice" };

			dbUnitCompo.initalizeTable(Paths.get(RESOUCE_PATH, folderName, TestConstant.INIT_XML_FILE_NAME));
			celeReplyServiceAPI.addCelebartionReply(getNormalReq(),CELEBRATION_NO,LOGIN_USER_NO,TestConstant.LOCALE_JA);

			// compare tables
			for (String table : COMPARED_TABLE_NAME) {
				// exclude to compare dateTime columns when celebration table
				if (table.equals("celebration_reply")) {
					dbUnitCompo.compareTable(Paths.get(RESOUCE_PATH, folderName, TestConstant.EXPECT_XML_FILE_NAME), table,
							TestConstant.DEFAULT_EXCLUDE_COLUMNS);
				} else if (table.equals("celebration_reply_notice")) {
					dbUnitCompo.compareTable(Paths.get(RESOUCE_PATH, folderName, TestConstant.EXPECT_XML_FILE_NAME), table,
							new String[] { "celebration_reply_no" });
				} else if(table.equals("user")) {
					dbUnitCompo.compareTable(Paths.get(RESOUCE_PATH, folderName, TestConstant.EXPECT_XML_FILE_NAME), table,
							TestConstant.DEFAULT_EXCLUDE_DATE_COLUMNS);
				} else {
					dbUnitCompo.compareTable(Paths.get(RESOUCE_PATH, folderName, TestConstant.EXPECT_XML_FILE_NAME), table);
				}
			}
			
			assertEquals(2, celeReplyNoticeRepo.findAll().size());
		}

		@ParameterizedTest
		@MethodSource("com.my.azusato.common.TestSource#locales")
		public void notExistUser_abnormal_case(Locale locale) throws Exception {
			String tableName = messageSource.getMessage(UserEntity.TABLE_NAME_KEY, null, locale);
			AzusatoException expect = new AzusatoException(HttpStatus.BAD_REQUEST, AzusatoException.I0005,
					messageSource.getMessage(AzusatoException.I0005, new String[] { tableName }, locale));

			AzusatoException result = Assertions.assertThrows(AzusatoException.class, () -> {
				celeReplyServiceAPI.addCelebartionReply(null, CELEBRATION_NO, 100000L ,locale);
			});

			assertEquals(expect, result);

		}
		
		

		private AddCelebrationReplyAPIReqeust getNormalReq() {
			return AddCelebrationReplyAPIReqeust.builder()
					.name(Entity.updatedVarChars[0]).content(Entity.createdVarChars[1])
					.build();
		}
	}
	
	
	@Nested
	class DeleteCelebationReply {

		final String RESOUCE_PATH = RESOUCE_BASIC_PATH + "deleteCelebration-reply/";
		
		final long CELEBRATION_NO = 1L;
		final long USER_NO = 1L;

		@Test
		public void givenNormal_result200() throws Exception {
			String folderName = "1";
			String[] COMPARED_TABLE_NAME = { "user", "celebration", "profile" , "celebration_reply"};
			
			dbUnitCompo.initalizeTable(Paths.get(RESOUCE_PATH, folderName, TestConstant.INIT_XML_FILE_NAME));
			
			celeReplyServiceAPI.deleteCelebartionReply(CELEBRATION_NO, USER_NO, TestConstant.LOCALE_JA);
			
			// compare tables
			for (String table : COMPARED_TABLE_NAME) {
				// exclude to compare dateTime columns when celebration table
				if(table.equals("user")) {
					dbUnitCompo.compareTable(Paths.get(RESOUCE_PATH, folderName, TestConstant.EXPECT_XML_FILE_NAME), table);
				} else {
					dbUnitCompo.compareTable(Paths.get(RESOUCE_PATH, folderName, TestConstant.EXPECT_XML_FILE_NAME), table,
							TestConstant.DEFAULT_EXCLUDE_UPDATE_DATE_COLUMNS);
				}
			}
		}

		@ParameterizedTest
		@MethodSource("com.my.azusato.common.TestSource#locales")
		public void givenDifferenceUser_resultError(Locale locale) throws Exception {
			String folderName = "2";
			
			dbUnitCompo.initalizeTable(Paths.get(RESOUCE_PATH, folderName, TestConstant.INIT_XML_FILE_NAME));
			
			AzusatoException expect = new AzusatoException(HttpStatus.BAD_REQUEST, AzusatoException.I0006,
					messageSource.getMessage(AzusatoException.I0006, null, locale));

			AzusatoException result = Assertions.assertThrows(AzusatoException.class, () -> {
				celeReplyServiceAPI.deleteCelebartionReply(CELEBRATION_NO, 1000L, locale);
			});
			
			assertEquals(expect, result);
			
			
		}

		@ParameterizedTest
		@MethodSource("com.my.azusato.common.TestSource#locales")
		public void givenNoCelebrationReplyData_resultError(Locale locale) throws Exception {
			
			String tableName = messageSource.getMessage(CelebrationReplyEntity.TABLE_NAME_KEY, null, locale);
			AzusatoException expect = new AzusatoException(HttpStatus.BAD_REQUEST, AzusatoException.I0005,
					messageSource.getMessage(AzusatoException.I0005, new String[] { tableName }, locale));

			AzusatoException result = Assertions.assertThrows(AzusatoException.class, () -> {
				celeReplyServiceAPI.deleteCelebartionReply(100000L, USER_NO, locale);
			});

			assertEquals(expect, result);

		}
	}
	
}
