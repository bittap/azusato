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
import org.springframework.test.context.transaction.TestTransaction;

import com.my.azusato.api.service.CelebrationReplyServiceAPI;
import com.my.azusato.api.service.request.AddCelebrationServiceAPIRequest;
import com.my.azusato.common.TestConstant;
import com.my.azusato.common.TestConstant.Entity;
import com.my.azusato.entity.CelebrationReplyEntity;
import com.my.azusato.entity.UserEntity;
import com.my.azusato.exception.AzusatoException;
import com.my.azusato.integration.AbstractIntegration;

public class CelebrationReplyServiceAPITest extends AbstractIntegration {

	@Autowired
	CelebrationReplyServiceAPI celeReplyServiceAPI;

	final String RESOUCE_BASIC_PATH = "src/test/data/unit/api/service/";

	@Nested
	class AddCelebration {

		final String RESOUCE_PATH = RESOUCE_BASIC_PATH + "addCelebration/";

		@Test
		public void admin_normal_case() throws Exception {
			String folderName = "1";
			String[] COMPARED_TABLE_NAME = { "user", "celebration" };

			dbUnitCompo.initalizeTable(Paths.get(RESOUCE_PATH, folderName, TestConstant.INIT_XML_FILE_NAME));
			celeReplyServiceAPI.addCelebartionAdmin(getNormalReq(), TestConstant.LOCALE_JA);

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
			celeReplyServiceAPI.addCelebartion(getNormalReq(), TestConstant.LOCALE_JA);

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
			celeReplyServiceAPI.addCelebartion(getNormalReq(), TestConstant.LOCALE_JA);

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
			AzusatoException expect = new AzusatoException(HttpStatus.BAD_REQUEST, AzusatoException.I0005,
					messageSource.getMessage(AzusatoException.I0005, new String[] { tableName }, locale));

			AzusatoException result = Assertions.assertThrows(AzusatoException.class, () -> {
				celeReplyServiceAPI.addCelebartion(req, locale);
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
				celeReplyServiceAPI.deleteCelebartionReply(CELEBRATION_NO, 1000L, TestConstant.LOCALE_JA);
			});
			
			TestTransaction.end();

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
