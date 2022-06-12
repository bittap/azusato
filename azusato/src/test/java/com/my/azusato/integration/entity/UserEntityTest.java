package com.my.azusato.integration.entity;

import java.nio.file.Paths;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import com.my.azusato.common.TestConstant;
import com.my.azusato.common.TestUtils;
import com.my.azusato.entity.ProfileEntity;
import com.my.azusato.entity.UserEntity;
import com.my.azusato.entity.UserEntity.Type;
import com.my.azusato.entity.common.CommonDateEntity;
import com.my.azusato.entity.common.CommonFlagEntity;
import com.my.azusato.integration.AbstractIntegration;

public class UserEntityTest extends AbstractIntegration {
	
	private static final Type OK_TYPE = Type.values()[0];
	
	private final String CURRENT_FOLDER = "user"; 
	
	@Nested
	class Save{

		@Test
		public void normal_test() throws Exception {
			// save data
			UserEntity savedData = save();

			UserEntity expect =	UserEntity.builder()
								.no(savedData.getNo())
								.id(TestConstant.Entity.createdVarChars[0])
								.password(TestConstant.Entity.createdVarChars[1])
								.name(TestConstant.Entity.createdVarChars[2])
								.userType(OK_TYPE.toString())
								.profile(insertedProfileData_normal_case(savedData.getNo()))
								.commonDate(CommonDateEntity.builder().createDatetime(TestConstant.Entity.createdDatetime).updateDatetime(TestConstant.Entity.updatedDatetime).build())
								.commonFlag(CommonFlagEntity.builder().deleteFlag(TestConstant.Entity.CreatedBoolean).build())
								.build();
			
			Assertions.assertEquals(expect, savedData);
		}
		
		@ParameterizedTest
		@EnumSource(value = Type.class)
		public void type_normal_test(Type type) throws Exception {
			// save data
			UserEntity savedData = save(type);
			
			UserEntity expect =	UserEntity.builder()
								.no(savedData.getNo())
								.id(TestConstant.Entity.createdVarChars[0])
								.password(TestConstant.Entity.createdVarChars[1])
								.name(TestConstant.Entity.createdVarChars[2])
								.userType(type.toString())
								.profile(insertedProfileData_normal_case(savedData.getNo()))
								.commonDate(CommonDateEntity.builder().createDatetime(TestConstant.Entity.createdDatetime).updateDatetime(TestConstant.Entity.updatedDatetime).build())
								.commonFlag(CommonFlagEntity.builder().deleteFlag(TestConstant.Entity.CreatedBoolean).build())
								.build();

			Assertions.assertEquals(expect, savedData);
		}
		
		private UserEntity save() throws Exception {
			return save(OK_TYPE);
		}
		
		private UserEntity save(Type type) throws Exception {
			// profile
			ProfileEntity pe = insertedProfileData_normal_case();
			// user
			UserEntity ue = insertedData_normal_case(type);
			pe.setUser(ue);
			ue.setProfile(pe);
			
			return userRepo.save(ue);
		}
	}
	
	@Nested
	class Update{
		private final Type UPDATE_TYPE = Type.values()[1];
		/**
		 * add data for a profile table
		 * @throws Exception
		 */
		@BeforeEach
		public void setUp() throws Exception {
			dbUnitCompo.initalizeTable(Paths.get(TestConstant.COMMON_ENTITY_FOLDER,CURRENT_FOLDER,"init","update.xml"));
		}
		
		@Test
		public void normal_test() throws Exception{
			List<UserEntity> results = userRepo.findAll();
			
			UserEntity savedData = TestUtils.getLastElement(results);
			
			savedData.setId(TestConstant.Entity.updatedVarChars[0]);
			savedData.setPassword(TestConstant.Entity.updatedVarChars[1]);
			savedData.setUserType(UPDATE_TYPE.toString());
			savedData.setName(TestConstant.Entity.updatedVarChars[2]);
			ProfileEntity savedProfileEntity = savedData.getProfile();
			savedProfileEntity.setImagePath(TestConstant.Entity.updatedVarChars[0]);
			savedData.setProfile(savedProfileEntity);
			
			savedData.setCommonDate(CommonDateEntity.builder().createDatetime(TestConstant.Entity.createdDatetime).updateDatetime(TestConstant.Entity.updatedDatetime).build());
			savedData.setCommonFlag(CommonFlagEntity.builder().deleteFlag(TestConstant.Entity.UpdatedBoolean).build());
			
			UserEntity result = userRepo.save(savedData);
			
			Assertions.assertEquals(getExpect(result.getNo()), result);
		}
		
		private UserEntity getExpect(long userNo) {
			ProfileEntity expectedProfile = new ProfileEntity();
			expectedProfile.setUserNo(userNo);
			expectedProfile.setImagePath(TestConstant.Entity.updatedVarChars[0]);
			
			UserEntity expectedUser = UserEntity.builder()
			.no(userNo)
			.id(TestConstant.Entity.updatedVarChars[0])
			.password(TestConstant.Entity.updatedVarChars[1])
			.name(TestConstant.Entity.updatedVarChars[2])
			.userType(UPDATE_TYPE.toString())
			.profile(expectedProfile)
			.commonDate(CommonDateEntity.builder().createDatetime(TestConstant.Entity.createdDatetime).updateDatetime(TestConstant.Entity.updatedDatetime).build())
			.commonFlag(CommonFlagEntity.builder().deleteFlag(TestConstant.Entity.UpdatedBoolean).build())
			.build();
			
			expectedProfile.setUser(expectedUser);
			
			return expectedUser;
		}
	}

	/**
	 * return inserted entity which is normal case.
	 */
	public ProfileEntity insertedProfileData_normal_case() throws Exception {
		return ProfileEntity.builder()
		.imagePath(TestConstant.Entity.createdVarChars[0])
		.build();
	}
	
	/**
	 * return inserted entity which is normal case.
	 */
	public static ProfileEntity insertedProfileData_normal_case(long userNo) throws Exception {
		return ProfileEntity.builder()
				.userNo(userNo)
				.imagePath(TestConstant.Entity.createdVarChars[0])
				.build();
	}

	/**
	 * return inserted entity which is normal case.
	 */
	private UserEntity insertedData_normal_case(Type type) throws Exception {
		return UserEntity.builder()
					.id(TestConstant.Entity.createdVarChars[0])
					.password(TestConstant.Entity.createdVarChars[1])
					.name(TestConstant.Entity.createdVarChars[2])
					.userType(type.toString())
					.commonDate(CommonDateEntity.builder().createDatetime(TestConstant.Entity.createdDatetime).updateDatetime(TestConstant.Entity.updatedDatetime).build())
					.commonFlag(CommonFlagEntity.builder().deleteFlag(TestConstant.Entity.CreatedBoolean).build())
					.build();
	}
}
