package com.my.azusato.integration.entity;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;

import com.my.azusato.common.TestConstant;
import com.my.azusato.common.TestUtils;
import com.my.azusato.dbunit.DBUnitComponent;
import com.my.azusato.entity.ProfileEntity;
import com.my.azusato.entity.UserEntity;
import com.my.azusato.entity.UserEntity.Type;
import com.my.azusato.entity.common.CommonDateEntity;
import com.my.azusato.entity.common.CommonFlagEntity;
import com.my.azusato.integration.AbstractIntegration;
import com.my.azusato.repository.ProfileRepository;
import com.my.azusato.repository.UserRepository;

public class UserEntityTest extends AbstractIntegration {
	
	@Autowired
	DBUnitComponent dbunit;
	
	@Autowired
	UserRepository userRepo;
	
	@Autowired
	ProfileRepository profileRepo;
	
	private static final Type OK_TYPE = Type.values()[0];
	
	/**
	 * add data for a profile table
	 * @throws Exception
	 */
	@BeforeEach
	public void setUp() throws Exception {
		dbunit.initalizeTable(TestConstant.COMMON_ENTITY_FOLDER + "user.xml");
	}
	
	@Nested
	class Save{
		
		@Test
		public void normal_test() throws Exception {
			userRepo.save(insertedData_normal_case());
			List<UserEntity> results = userRepo.findAll();
			
			UserEntity savedData = TestUtils.getLastElement(results);
			
			UserEntity expect =	UserEntity.builder()
								.no(savedData.getNo())
								.id(TestConstant.Entity.createdVarChars[0])
								.password(TestConstant.Entity.createdVarChars[1])
								.userType(OK_TYPE.toString())
								.profile(expectedProfileEntity())
								.commonDate(CommonDateEntity.builder().createDatetime(TestConstant.Entity.createdDatetime).updateDatetime(TestConstant.Entity.updatedDatetime).build())
								.commonFlag(CommonFlagEntity.builder().deleteFlag(TestConstant.Entity.CreatedBoolean).build())
								.build();

			Assertions.assertEquals(expect, savedData);
		}
		
		@ParameterizedTest
		@EnumSource(value = Type.class)
		public void type_normal_test(Type type) throws Exception {
			userRepo.save(insertedData_normal_case(type));
			List<UserEntity> results = userRepo.findAll();
			
			UserEntity savedData = TestUtils.getLastElement(results);
			
			UserEntity expect =	UserEntity.builder()
								.no(savedData.getNo())
								.id(TestConstant.Entity.createdVarChars[0])
								.password(TestConstant.Entity.createdVarChars[1])
								.userType(type.toString())
								.profile(expectedProfileEntity())
								.commonDate(CommonDateEntity.builder().createDatetime(TestConstant.Entity.createdDatetime).updateDatetime(TestConstant.Entity.updatedDatetime).build())
								.commonFlag(CommonFlagEntity.builder().deleteFlag(TestConstant.Entity.CreatedBoolean).build())
								.build();

			Assertions.assertEquals(expect, savedData);
		}
	}
	
	@Nested
	class Update{
		private final Type UPDATE_TYPE = Type.values()[1];
		private final LocalDateTime updatedDatetimeForTest = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
		
		@Test
		public void normal_test() throws Exception{
			userRepo.save(insertedData_normal_case());
			List<UserEntity> results = userRepo.findAll();
			
			UserEntity savedData = TestUtils.getLastElement(results);
			
			savedData.setId(TestConstant.Entity.updatedVarChars[0]);
			savedData.setPassword(TestConstant.Entity.updatedVarChars[1]);
			savedData.setUserType(UPDATE_TYPE.toString());
			
			ProfileEntity savedProfileEntity = savedData.getProfile();
			savedProfileEntity.setImageBase64(TestConstant.Entity.updatedVarChars[2]);
			savedProfileEntity.setImageType(TestConstant.Entity.updatedVarChars[3]);
			savedProfileEntity.getCommonDate().setUpdateDatetime(updatedDatetimeForTest);
			savedProfileEntity.setCommonDate(savedProfileEntity.getCommonDate());
			savedProfileEntity.setCommonFlag(CommonFlagEntity.builder().deleteFlag(TestConstant.Entity.UpdatedBoolean).build());
			savedData.setProfile(savedProfileEntity);
			
			savedData.setCommonDate(CommonDateEntity.builder().createDatetime(TestConstant.Entity.createdDatetime).updateDatetime(updatedDatetimeForTest).build());
			savedData.setCommonFlag(CommonFlagEntity.builder().deleteFlag(TestConstant.Entity.UpdatedBoolean).build());
			
			UserEntity result = userRepo.save(savedData);
			
			Assertions.assertEquals(getExpect(result.getNo(),result.getProfile().getNo()), result);
		}
		
		private UserEntity getExpect(long userNo, long profileNo) {
			ProfileEntity expectedProfile = new ProfileEntity();
			expectedProfile.setNo(profileNo);
			expectedProfile.setImageBase64(TestConstant.Entity.updatedVarChars[2]);
			expectedProfile.setImageType(TestConstant.Entity.updatedVarChars[3]);
			expectedProfile.setCommonDate(CommonDateEntity.builder().createDatetime(TestConstant.Entity.createdDatetime).updateDatetime(updatedDatetimeForTest).build());
			expectedProfile.setCommonFlag(CommonFlagEntity.builder().deleteFlag(TestConstant.Entity.UpdatedBoolean).build());
			
			return UserEntity.builder()
					.no(userNo)
					.id(TestConstant.Entity.updatedVarChars[0])
					.password(TestConstant.Entity.updatedVarChars[1])
					.userType(UPDATE_TYPE.toString())
					.profile(expectedProfile)
					.commonDate(CommonDateEntity.builder().createDatetime(TestConstant.Entity.createdDatetime).updateDatetime(updatedDatetimeForTest).build())
					.commonFlag(CommonFlagEntity.builder().deleteFlag(TestConstant.Entity.UpdatedBoolean).build())
					.build();
		}
	}
	
	@AfterEach
	public void after() throws Exception {
		dbunit.deleteTable();
	}
	
	
	private ProfileEntity expectedProfileEntity() {
		return profileRepo.findAll().get(0);
	}

	/**
	 * return inserted entity which is normal case.
	 */
	private UserEntity insertedData_normal_case() throws Exception {
		return insertedData_normal_case(OK_TYPE);
	}
	
	/**
	 * return inserted entity which is normal case.
	 */
	private UserEntity insertedData_normal_case(Type type) throws Exception {
		return UserEntity.builder()
					.id(TestConstant.Entity.createdVarChars[0])
					.password(TestConstant.Entity.createdVarChars[1])
					.profile(expectedProfileEntity())
					.userType(type.toString())
					.commonDate(CommonDateEntity.builder().createDatetime(TestConstant.Entity.createdDatetime).updateDatetime(TestConstant.Entity.updatedDatetime).build())
					.commonFlag(CommonFlagEntity.builder().deleteFlag(TestConstant.Entity.CreatedBoolean).build())
					.build();
	}
}
