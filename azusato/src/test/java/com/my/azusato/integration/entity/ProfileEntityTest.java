package com.my.azusato.integration.entity;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.transaction.Transactional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.my.azusato.common.TestConstant;
import com.my.azusato.common.TestUtils;
import com.my.azusato.entity.ProfileEntity;
import com.my.azusato.entity.common.CommonDateEntity;
import com.my.azusato.entity.common.CommonFlagEntity;
import com.my.azusato.repository.ProfileRepository;

@SpringBootTest
@ActiveProfiles(TestConstant.PROFILES)
@Transactional // for rollback
public class ProfileEntityTest {
	
	@Autowired
	ProfileRepository profileRepo;
	
	@Nested
	class Save{
		
		@ParameterizedTest
		@MethodSource("com.my.azusato.integration.entity.ProfileEntityTest#save")
		public void normal_case(ProfileEntity insertedEntity, ProfileEntity expect) throws Exception {
			profileRepo.save(insertedEntity);
			
			List<ProfileEntity> results = profileRepo.findAll();
			
			ProfileEntity result = TestUtils.getLastElement(results);
			// exclude difficult values for comparing
			result = TestUtils.excludeColumn(result);
			
			Assertions.assertEquals(expect, result);
		}
	}
	
	@Nested
	class Update{
		
		private final LocalDateTime updatedNowForTest = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
		
		@Test
		public void normal_case() throws Exception {
			ProfileEntity entity = insertedData_normal_case();
					
			profileRepo.save(entity);
			
			ProfileEntity expect = ProfileEntity.builder()
			.ImageBase64(TestConstant.Entity.updatedVarChars[0])
			.ImageType(TestConstant.Entity.updatedVarChars[1])
			.commonDate(CommonDateEntity.builder()
					.createDatetime(TestConstant.Entity.createdNow)
					.updateDatetime(updatedNowForTest)
					.build())
			.commonFlag(CommonFlagEntity.builder()
					.deleteFlag(TestConstant.Entity.UpdatedBoolean)
					.build())
			.build();
			
			List<ProfileEntity> results = profileRepo.findAll();
			
			ProfileEntity savedData = TestUtils.getLastElement(results);
			
			savedData.setImageBase64(TestConstant.Entity.updatedVarChars[0]);
			savedData.setImageType(TestConstant.Entity.updatedVarChars[1]);
			savedData.getCommonDate().setUpdateDatetime(updatedNowForTest);
			savedData.setCommonDate(savedData.getCommonDate());
			savedData.setCommonFlag(CommonFlagEntity.builder().deleteFlag(TestConstant.Entity.UpdatedBoolean).build());
			
			ProfileEntity result = profileRepo.save(savedData);
			// exclude difficult values for comparing
			result = TestUtils.excludeColumn(result);
			
			Assertions.assertEquals(expect, result);
		}
	}

	private static String getBase64() throws Exception {
		Path path = Paths.get(TestConstant.COMMON_TEST_DATA_FOLDER,"base64OfImage(PNG).txt");
		return Files.lines(path).collect(Collectors.joining());
	}
	
	/**
	 * return inserted entity which is normal case.
	 */
	public static ProfileEntity insertedData_normal_case() throws Exception {
		return ProfileEntity.builder()
		.ImageBase64(getBase64()).ImageType(TestConstant.Entity.ImageType[0])
		.commonDate(CommonDateEntity.builder().createDatetime(TestConstant.Entity.createdNow).updateDatetime(TestConstant.Entity.updatedNow).build())
		.commonFlag(CommonFlagEntity.builder().deleteFlag(true).build())
		.build();
	}
	
	/**
	 * return inserted entity which is normal case.
	 */
	public static ProfileEntity insertedData_normal_case(long no) throws Exception {
		return ProfileEntity.builder()
				.no(no)
				.ImageBase64(getBase64()).ImageType(TestConstant.Entity.ImageType[0])
				.commonDate(CommonDateEntity.builder().createDatetime(TestConstant.Entity.createdNow).updateDatetime(TestConstant.Entity.updatedNow).build())
				.commonFlag(CommonFlagEntity.builder().deleteFlag(true).build())
				.build();
	}
	
	@SuppressWarnings("unused")
	private static Stream<Arguments> save() throws Exception{
		return Stream.of(
				// normal scenario
				Arguments.of(
						// inserted
						insertedData_normal_case()
						,
						// Expect
						ProfileEntity.builder()
						.ImageBase64(getBase64()).ImageType(TestConstant.Entity.ImageType[0])
						.commonDate(CommonDateEntity.builder().createDatetime(TestConstant.Entity.createdNow).updateDatetime(TestConstant.Entity.updatedNow).build())
						.commonFlag(CommonFlagEntity.builder().deleteFlag(TestConstant.Entity.CreatedBoolean).build())
						.build())
		);		
	}
}
