package com.my.azusato.unit.api.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.FileInputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import com.my.azusato.api.service.ProfileServiceAPI;
import com.my.azusato.api.service.request.ModifyUserProfileServiceAPIRequest;
import com.my.azusato.common.TestConstant;
import com.my.azusato.common.TestConstant.Entity;
import com.my.azusato.entity.UserEntity;
import com.my.azusato.exception.AzusatoException;
import com.my.azusato.integration.AbstractIntegration;

public class ProfileSerivceAPITest extends AbstractIntegration {

	@Autowired
	ProfileServiceAPI targetService;
	
	final String RESOUCE_BASIC_PATH = "src/test/data/unit/api/service/";
	
	@Nested
	class GetDefaultProfilePath {

		/**
		 * およそ10000回を実行し、もし引っかからないものがあればfailにする。
		 * 
		 * @throws Exception
		 */
		@Test
		public void getDefaultProfileBase64_normal_case() throws Exception {
			Map<String, Boolean> expect = expect();
			System.out.printf("expect : %s\n",expect);
			for (int i = 0; i < 10000; i++) {
				String result = targetService.getDefaultProfilePath();
	
				if(expect.containsKey(result)) {
					expect.put(result, true);
				}
			}

			expect.forEach((k,v)->{
				Assertions.assertTrue(v);
			});
		}
		
	}

	@Nested
	class UpdateUserProfile {
		
		final String RESOUCE_PATH = RESOUCE_BASIC_PATH + "updateUserProfile/";
		
		@Test
		public void givenPng_resultOk() throws Exception {
			String folderName = "1";
			String expectFileName = "1."+Entity.ImageType[0];
			
			dbUnitCompo.initalizeTable(Paths.get(RESOUCE_PATH, folderName, TestConstant.INIT_XML_FILE_NAME));
			
			ModifyUserProfileServiceAPIRequest req = ModifyUserProfileServiceAPIRequest.builder()
			.profileImage(new FileInputStream(TestConstant.TEST_IMAGE_PATH))
			.profileImageType(Entity.ImageType[0])
			.userNo(Entity.createdLongs[0])
			.build();
				
			targetService.updateUserProfile(req, TestConstant.LOCALE_JA);
			
			String[] COMPARED_TABLE_NAME = { "user", "profile" };
			
			// compare tables
			for (String table : COMPARED_TABLE_NAME) {
				// exclude to compare dateTime columns when celebration table
				if (table.equals("user")) {
					dbUnitCompo.compareTable(Paths.get(RESOUCE_PATH, folderName, TestConstant.EXPECT_XML_FILE_NAME), table,
							TestConstant.DEFAULT_EXCLUDE_UPDATE_DATE_COLUMNS);
				}else {
					dbUnitCompo.compareTable(Paths.get(RESOUCE_PATH, folderName, TestConstant.EXPECT_XML_FILE_NAME), table);
				}
			}
			
			// ファイル存在有無チェック
			Assertions.assertDoesNotThrow(()->{
				Paths.get(profileProperty.getClientImageFolderPath() + expectFileName );
			}); 
		}
		
		@Test
		public void givenJpeg_resultOk() throws Exception {
			String folderName = "2";
			String expectFileName = "1."+Entity.ImageType[1];
			
			dbUnitCompo.initalizeTable(Paths.get(RESOUCE_PATH, folderName, TestConstant.INIT_XML_FILE_NAME));
			
			ModifyUserProfileServiceAPIRequest req = ModifyUserProfileServiceAPIRequest.builder()
			.profileImage(new FileInputStream(TestConstant.TEST_IMAGE_PATH))
			.profileImageType(Entity.ImageType[1])
			.userNo(Entity.createdLongs[0])
			.build();
				
			targetService.updateUserProfile(req, TestConstant.LOCALE_JA);
			
			String[] COMPARED_TABLE_NAME = { "user", "profile" };
			
			// compare tables
			for (String table : COMPARED_TABLE_NAME) {
				// exclude to compare dateTime columns when celebration table
				if (table.equals("user")) {
					dbUnitCompo.compareTable(Paths.get(RESOUCE_PATH, folderName, TestConstant.EXPECT_XML_FILE_NAME), table,
							TestConstant.DEFAULT_EXCLUDE_UPDATE_DATE_COLUMNS);
				}else {
					dbUnitCompo.compareTable(Paths.get(RESOUCE_PATH, folderName, TestConstant.EXPECT_XML_FILE_NAME), table);
				}
			}
			
			// ファイル存在有無チェック
			Assertions.assertDoesNotThrow(()->{
				Paths.get(profileProperty.getClientImageFolderPath() + expectFileName );
			}); 
		}
		
		@ParameterizedTest
		@MethodSource("com.my.azusato.common.TestSource#locales")
		public void notExistUser_result400(Locale locale) throws Exception {
			
			ModifyUserProfileServiceAPIRequest req = ModifyUserProfileServiceAPIRequest.builder()
			.userNo(100000L)
			.build();
			
			String tableName = messageSource.getMessage(UserEntity.TABLE_NAME_KEY, null, locale);
			AzusatoException expect = new AzusatoException(HttpStatus.BAD_REQUEST, AzusatoException.I0005,
					messageSource.getMessage(AzusatoException.I0005, new String[] { tableName }, locale));


			AzusatoException result = Assertions.assertThrows(AzusatoException.class, () -> {
				targetService.updateUserProfile(req, locale);
			});

			assertEquals(expect, result);

		}
		
	}
	
	public Map<String, Boolean> expect(){
		Path path = Paths.get("src","main","resources","static","image","default","profile");
		System.out.printf("absolutPath: %s\n",path.toAbsolutePath());
		List<String> fileNames = Arrays.asList(path.toFile().list());

		return fileNames.stream().collect(Collectors.toMap((e)->{
			return Paths.get(profileProperty.getClientDefaultImageFolderPath(),e).toString();
		}, (e)->{
			return Boolean.valueOf(false);
		}));

	}
}
