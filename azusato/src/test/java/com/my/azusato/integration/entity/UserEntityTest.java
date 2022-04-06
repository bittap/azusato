package com.my.azusato.integration.entity;

import java.time.LocalDateTime;

import javax.transaction.Transactional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.my.azusato.common.TestConstant;
import com.my.azusato.entity.UserEntity;
import com.my.azusato.entity.UserEntity.Type;
import com.my.azusato.repository.UserRepository;

@SpringBootTest
@ActiveProfiles(TestConstant.PROFILES)
@Transactional
public class UserEntityTest {
	
	@Autowired
	UserRepository userRepo;
	
	private final String OK_ID = "ok_id"; 
	
	private final String OK_PASSWORD = "OK_PASSWORD"; 
	
	private final LocalDateTime OK_DATETIME = LocalDateTime.now();
	
	private final long TARGET_NO = 1;

	@Test
	public void whenSaved_givenSelect_returnSavedData() {
		UserEntity entity = UserEntity.builder()
									.id(OK_ID)
									.password(OK_PASSWORD)
									.userType(Type.admin)
									.createDatetime(OK_DATETIME)
									.updateDatetime(OK_DATETIME)
									.build();
		
		userRepo.save(entity);
									
		UserEntity expect = UserEntity.builder()
								.id(OK_ID)
								.password(OK_PASSWORD)
								.profile(null)
								.userType(Type.admin)
								.createDatetime(OK_DATETIME)
								.updateDatetime(OK_DATETIME)
								.build();
		UserEntity result = userRepo.findById(TARGET_NO).get();
		
		Assertions.assertEquals(expect, result);
	}
}
