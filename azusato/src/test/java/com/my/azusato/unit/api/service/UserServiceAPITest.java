package com.my.azusato.unit.api.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.util.ReflectionTestUtils;

import com.my.azusato.api.controller.UserControllerAPI;
import com.my.azusato.api.service.UserServiceAPI;
import com.my.azusato.api.service.request.AddNonMemberUserServiceAPIRequest;
import com.my.azusato.common.TestConstant.Entity;
import com.my.azusato.entity.UserEntity;
import com.my.azusato.entity.UserEntity.Type;
import com.my.azusato.integration.AbstractIntegration;
import com.my.azusato.repository.UserRepository;
import com.my.azusato.view.controller.common.ValueConstant;

public class UserServiceAPITest extends AbstractIntegration {

	@Autowired
	UserServiceAPI userServiceAPI;

	@Autowired
	UserControllerAPI userControllerAPI;

	@Autowired
	UserRepository userRepository;

	@Nested
	class AddNonMember {

		@Test
		public void normal_case() throws Exception {
			LocalDateTime beforeExecuteTime = LocalDateTime.now();
			String id = ReflectionTestUtils.invokeMethod(userControllerAPI, "getNonMemberRandomString");
			AddNonMemberUserServiceAPIRequest serviceReq = AddNonMemberUserServiceAPIRequest.builder()
					.name(Entity.createdVarChars[0]).profileImageBase64(Entity.createdVarChars[1])
					.profileImageType(Entity.createdVarChars[2]).id(id).build();

			userServiceAPI.addNonMember(serviceReq);

			UserEntity result = userRepository.findAll().get(Entity.GET_INDEXS[0]);

			assertEquals(id, result.getId());
			assertEquals(Entity.createdVarChars[0], result.getName());
			assertEquals(Entity.createdVarChars[1], result.getProfile().getImageBase64());
			assertEquals(Entity.createdVarChars[2], result.getProfile().getImageType());
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
