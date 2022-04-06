package com.my.azusato.integration.entity;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import javax.transaction.Transactional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.my.azusato.common.TestConstant;
import com.my.azusato.common.TestUtils;
import com.my.azusato.entity.ProfileEntity;
import com.my.azusato.entity.UserEntity;
import com.my.azusato.entity.UserEntity.Type;
import com.my.azusato.entity.common.CommonDateEntity;
import com.my.azusato.entity.common.CommonFlagEntity;
import com.my.azusato.repository.UserRepository;

@SpringBootTest
@ActiveProfiles(TestConstant.PROFILES)
@Transactional // for rollback
public class UserEntityTest {
	
	@Autowired
	UserRepository userRepo;
	
	private static final Type OK_TYPE = Type.values()[0];
	
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
								.userType(OK_TYPE)
								.profile(ProfileEntityTest.insertedData_normal_case())
								.commonDate(CommonDateEntity.builder().createDatetime(TestConstant.Entity.createdNow).updateDatetime(TestConstant.Entity.updatedNow).build())
								.commonFlag(CommonFlagEntity.builder().deleteFlag(TestConstant.Entity.CreatedBoolean).build())
								.build();
			
			// exclude difficult values for comparing
			TestUtils.excludeColumn(savedData.getProfile());
			
			Assertions.assertEquals(expect, savedData);
		}
	}
	
	@Nested
	class Update{
		private final Type UPDATE_TYPE = Type.values()[1];
		private final LocalDateTime updatedNowForTest = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
		
		@Test
		public void normal_test() throws Exception{
			userRepo.save(insertedData_normal_case());
			List<UserEntity> results = userRepo.findAll();
			
			UserEntity savedData = TestUtils.getLastElement(results);
			
			savedData.setId(TestConstant.Entity.updatedVarChars[0]);
			savedData.setPassword(TestConstant.Entity.updatedVarChars[1]);
			savedData.setUserType(UPDATE_TYPE);
			
			ProfileEntity savedProfileEntity = savedData.getProfile();
			savedProfileEntity.setImageBase64(TestConstant.Entity.updatedVarChars[2]);
			savedProfileEntity.setImageType(TestConstant.Entity.updatedVarChars[3]);
			savedProfileEntity.getCommonDate().setUpdateDatetime(updatedNowForTest);
			savedProfileEntity.setCommonDate(savedData.getCommonDate());
			savedProfileEntity.setCommonFlag(CommonFlagEntity.builder().deleteFlag(TestConstant.Entity.UpdatedBoolean).build());
			savedData.setProfile(savedProfileEntity);
			
			savedData.setCommonDate(CommonDateEntity.builder().createDatetime(TestConstant.Entity.createdNow).updateDatetime(updatedNowForTest).build());
			savedData.setCommonFlag(CommonFlagEntity.builder().deleteFlag(TestConstant.Entity.UpdatedBoolean).build());
			
			UserEntity result = userRepo.save(savedData);
			
			Assertions.assertEquals(getExpect(result.getNo(),result.getProfile().getNo()), result);
		}
		
		private UserEntity getExpect(long userNo, long profileNo) {
			ProfileEntity expectedProfile = new ProfileEntity();
			expectedProfile.setNo(profileNo);
			expectedProfile.setImageBase64(TestConstant.Entity.updatedVarChars[2]);
			expectedProfile.setImageType(TestConstant.Entity.updatedVarChars[3]);
			expectedProfile.setCommonDate(CommonDateEntity.builder().createDatetime(TestConstant.Entity.createdNow).updateDatetime(updatedNowForTest).build());
			expectedProfile.setCommonDate(expectedProfile.getCommonDate());
			expectedProfile.setCommonFlag(CommonFlagEntity.builder().deleteFlag(TestConstant.Entity.UpdatedBoolean).build());
			
			return UserEntity.builder()
					.no(userNo)
					.id(TestConstant.Entity.updatedVarChars[0])
					.password(TestConstant.Entity.updatedVarChars[1])
					.userType(UPDATE_TYPE)
					.profile(expectedProfile)
					.commonDate(CommonDateEntity.builder().createDatetime(TestConstant.Entity.createdNow).updateDatetime(updatedNowForTest).build())
					.commonFlag(CommonFlagEntity.builder().deleteFlag(TestConstant.Entity.UpdatedBoolean).build())
					.build();
		}
	}
	
	/**
	 * return inserted entity which is normal case.
	 */
	public static UserEntity insertedData_normal_case() throws Exception {
		return UserEntity.builder()
					.id(TestConstant.Entity.createdVarChars[0])
					.password(TestConstant.Entity.createdVarChars[1])
					.profile(ProfileEntityTest.insertedData_normal_case())
					.userType(OK_TYPE)
					.commonDate(CommonDateEntity.builder().createDatetime(TestConstant.Entity.createdNow).updateDatetime(TestConstant.Entity.updatedNow).build())
					.commonFlag(CommonFlagEntity.builder().deleteFlag(TestConstant.Entity.CreatedBoolean).build())
					.build();
	}
}
