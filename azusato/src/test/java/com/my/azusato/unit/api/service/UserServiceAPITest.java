package com.my.azusato.unit.api.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Locale;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;

import com.my.azusato.api.controller.UserControllerAPI;
import com.my.azusato.api.service.UserServiceAPI;
import com.my.azusato.api.service.request.AddNonMemberUserServiceAPIRequest;
import com.my.azusato.api.service.response.GetSessionUserServiceAPIResponse;
import com.my.azusato.common.TestConstant;
import com.my.azusato.common.TestConstant.Entity;
import com.my.azusato.entity.UserEntity;
import com.my.azusato.entity.UserEntity.Type;
import com.my.azusato.exception.AzusatoException;
import com.my.azusato.integration.AbstractIntegration;
import com.my.azusato.view.controller.common.ValueConstant;

public class UserServiceAPITest extends AbstractIntegration {

	@Autowired
	UserServiceAPI userServiceAPI;

	@Autowired
	UserControllerAPI userControllerAPI;
	
	final String RESOUCE_BASIC_PATH = "src/test/data/unit/api/service/";
	
	@Nested
	class getSessionUser {
		
		final String RESOUCE_PATH = RESOUCE_BASIC_PATH + "getSessionUser/";
		
		@Test
		public void givenNormalValue_resultOk() throws Exception {
			String folderName = "1";
			dbUnitCompo.initalizeTable(Paths.get(RESOUCE_PATH, folderName, TestConstant.INIT_XML_FILE_NAME));
			GetSessionUserServiceAPIResponse result = userServiceAPI.getSessionUser(Long.valueOf(Entity.createdInts[0]), null);
			
			GetSessionUserServiceAPIResponse expect = GetSessionUserServiceAPIResponse.builder()
												.id(Entity.createdVarChars[0])
												.name(Entity.createdVarChars[2])
												.profileImagePath(Entity.createdVarChars[0])
												.build();
			
			assertEquals(expect,result);
		}
		
		@ParameterizedTest
		@MethodSource("com.my.azusato.common.TestSource#locales")
		public void givenNotExistData_result500Error(Locale locale) throws Exception {
			
			AzusatoException result = Assertions.assertThrows(AzusatoException.class, ()->userServiceAPI.getSessionUser(Long.valueOf(Entity.createdInts[0]), locale));

			String tableName = messageSource.getMessage(UserEntity.TABLE_NAME_KEY, null, locale);
			AzusatoException expect = new AzusatoException(HttpStatus.BAD_REQUEST, AzusatoException.I0005,
					messageSource.getMessage(AzusatoException.I0005, new String[] { tableName }, locale));
			
			assertEquals(expect,result);
		}
	}

	@Nested
	class AddNonMember {

		@Test
		public void normal_case() throws Exception {
			LocalDateTime beforeExecuteTime = LocalDateTime.now();
			AddNonMemberUserServiceAPIRequest serviceReq = AddNonMemberUserServiceAPIRequest.builder()
					.name(Entity.createdVarChars[0]).id(Entity.createdVarChars[1]).build();

			userServiceAPI.addNonMember(serviceReq);

			UserEntity result = userRepo.findAll().get(Entity.GET_INDEXS[0]);

			assertEquals(Entity.createdVarChars[1], result.getId());
			assertEquals(Entity.createdVarChars[0], result.getName());
			assertTrue(beforeExecuteTime.isBefore(result.getCommonDate().getCreateDatetime()));
			assertTrue(beforeExecuteTime.isBefore(result.getCommonDate().getUpdateDatetime()));
			assertEquals(Type.nonmember.toString(), result.getUserType());
			assertEquals(ValueConstant.DEFAULT_DELETE_FLAG, result.getCommonFlag().getDeleteFlag());

			/*
			 * cookie test can't this because of not http invoking
			 */
		}
	}
}
