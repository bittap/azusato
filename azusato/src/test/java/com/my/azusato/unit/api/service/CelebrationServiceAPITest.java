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
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;

import com.my.azusato.api.service.CelebrationServiceAPI;
import com.my.azusato.api.service.request.AddCelebrationServiceAPIRequest;
import com.my.azusato.common.TestConstant;
import com.my.azusato.common.TestConstant.Entity;
import com.my.azusato.dbunit.DBUnitComponent;
import com.my.azusato.entity.UserEntity;
import com.my.azusato.exception.AzusatoException;
import com.my.azusato.integration.AbstractIntegration;
import com.my.azusato.repository.CelebrationNoticeRepository;
import com.my.azusato.repository.UserRepository;

public class CelebrationServiceAPITest extends AbstractIntegration {

	@Autowired
	CelebrationServiceAPI celeServiceAPI;

	@Autowired
	DBUnitComponent dbCompo;

	@Autowired
	UserRepository userRepository;

	@Autowired
	CelebrationNoticeRepository celeNoticeRepo;

	final String RESOUCE_BASIC_PATH = "src/test/data/unit/api/service/";

	@Autowired
	MessageSource messageSource;

	@Nested
	class AddCelebration {

		final String RESOUCE_PATH = RESOUCE_BASIC_PATH + "addCelebration/";

		@Test
		public void admin_normal_case() throws Exception {
			String folderName = "1";
			String[] COMPARED_TABLE_NAME = { "user", "celebration" };

			dbCompo.initalizeTable(Paths.get(RESOUCE_PATH, folderName, TestConstant.INIT_XML_FILE_NAME));
			celeServiceAPI.addCelebartionAdmin(getNormalReq(), TestConstant.LOCALE_JA);

			// compare tables
			for (String table : COMPARED_TABLE_NAME) {
				// exclude to compare dateTime columns when celebration table
				if (table.equals("celebration")) {
					dbCompo.compareTable(Paths.get(RESOUCE_PATH, folderName, TestConstant.EXPECT_XML_FILE_NAME), table,
							TestConstant.DEFAULT_EXCLUDE_COLUMNS);
				} else {
					dbCompo.compareTable(Paths.get(RESOUCE_PATH, folderName, TestConstant.EXPECT_XML_FILE_NAME), table);
				}
			}

		}

		@Test
		public void normal_case() throws Exception {
			String folderName = "2";
			String[] COMPARED_TABLE_NAME = { "user", "celebration", "celebration_notice" };

			dbCompo.initalizeTable(Paths.get(RESOUCE_PATH, folderName, TestConstant.INIT_XML_FILE_NAME));
			celeServiceAPI.addCelebartion(getNormalReq(), TestConstant.LOCALE_JA);

			// compare tables
			for (String table : COMPARED_TABLE_NAME) {
				// exclude to compare dateTime columns when celebration table
				if (table.equals("celebration")) {
					dbCompo.compareTable(Paths.get(RESOUCE_PATH, folderName, TestConstant.EXPECT_XML_FILE_NAME), table,
							TestConstant.DEFAULT_EXCLUDE_COLUMNS);
				} else if (table.equals("celebration_notice")) {
					dbCompo.compareTable(Paths.get(RESOUCE_PATH, folderName, TestConstant.EXPECT_XML_FILE_NAME), table,
							new String[] { "celebration_no" });
				} else {
					dbCompo.compareTable(Paths.get(RESOUCE_PATH, folderName, TestConstant.EXPECT_XML_FILE_NAME), table);
				}
			}

		}

		@Test
		public void notExistAdmin_normal_case() throws Exception {
			String folderName = "3";
			String[] COMPARED_TABLE_NAME = { "user", "celebration" };

			dbCompo.initalizeTable(Paths.get(RESOUCE_PATH, folderName, TestConstant.INIT_XML_FILE_NAME));
			celeServiceAPI.addCelebartion(getNormalReq(), TestConstant.LOCALE_JA);

			// compare tables
			for (String table : COMPARED_TABLE_NAME) {
				// exclude to compare dateTime columns when celebration table
				if (table.equals("celebration")) {
					dbCompo.compareTable(Paths.get(RESOUCE_PATH, folderName, TestConstant.EXPECT_XML_FILE_NAME), table,
							TestConstant.DEFAULT_EXCLUDE_COLUMNS);
				} else {
					dbCompo.compareTable(Paths.get(RESOUCE_PATH, folderName, TestConstant.EXPECT_XML_FILE_NAME), table);
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
			return AddCelebrationServiceAPIRequest.builder().title(Entity.createdVarChars[0])
					.content(Entity.createdVarChars[1]).userNo(Entity.createdInts[0]).build();
		}
	}
}
