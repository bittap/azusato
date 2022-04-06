package com.my.azusato.integration.entity;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.my.azusato.common.TestConstant;
import com.my.azusato.entity.ProfileEntity;
import com.my.azusato.entity.common.CommonDateEntity;
import com.my.azusato.entity.common.CommonFlagEntity;
import com.my.azusato.repository.ProfileRepository;

@SpringBootTest
@ActiveProfiles(TestConstant.PROFILES)
@Transactional
public class ProfileEntityTest {
	
	@Autowired
	ProfileRepository profileRepo;
	
	private static final String DEFAULT_IMAGE_EXTENTION = "png";
	
	private static final LocalDateTime createdNow = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
	
	private static final LocalDateTime updateNow = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
	
	@Nested
	class Save{
		
		@ParameterizedTest
		@MethodSource("com.my.azusato.integration.entity.ProfileEntityTest#save")
		public void normal_case(ProfileEntity insertedEntity, ProfileEntity expect) throws IOException {
			profileRepo.save(insertedEntity);
			
			List<ProfileEntity> results = profileRepo.findAll();
			
			ProfileEntity result = results.stream().sorted((e1,e2)->{
				return (int) (e2.getNo() - e1.getNo());
			}).findFirst().get();
			
			// exclude difficult values for comparing
			result.setNo(null);
			Assertions.assertEquals(expect, result);
		}
	}
	
	@Nested
	class Update{
		
		private final String updatedBase64 = "updateBase64";
		private final String updatedType = "updateType";
		private final boolean updatedFlag = false;
		private final LocalDateTime updatedNow = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
		
		@Test
		public void normal_case() throws IOException {
			ProfileEntity entity = insertedData_normal_case();
					
			profileRepo.save(entity);
			
			ProfileEntity expect = ProfileEntity.builder()
			.ImageBase64(updatedBase64)
			.ImageType(updatedType)
			.commonDate(CommonDateEntity.builder()
					.createDatetime(createdNow)
					.updateDatetime(updatedNow)
					.build())
			.commonFlag(CommonFlagEntity.builder()
					.deleteFlag(updatedFlag)
					.build())
			.build();
			
			List<ProfileEntity> results = profileRepo.findAll();
			
			ProfileEntity savedData = results.stream().sorted((e1,e2)->{
			return (int) (e2.getNo() - e1.getNo());
			}).findFirst().get();
			
			savedData.setImageBase64(updatedBase64);
			savedData.setImageType(updatedType);
			savedData.getCommonDate().setUpdateDatetime(updatedNow);
			savedData.setCommonDate(savedData.getCommonDate());
			savedData.setCommonFlag(CommonFlagEntity.builder().deleteFlag(updatedFlag).build());
			
			ProfileEntity result = profileRepo.save(savedData);
			
			// exclude difficult values for comparing
			result.setNo(null);
			Assertions.assertEquals(expect, result);
		}
	}

	private static String getBase64() throws IOException {
		Path path = Paths.get(TestConstant.COMMON_TEST_DATA_FOLDER,"base64OfImage(PNG).txt");
		return Files.lines(path).collect(Collectors.joining());
	}
	
	/**
	 * return inserted entity which is normal case.
	 */
	private static ProfileEntity insertedData_normal_case() throws IOException {
		return ProfileEntity.builder()
		.ImageBase64(getBase64()).ImageType(DEFAULT_IMAGE_EXTENTION)
		.commonDate(CommonDateEntity.builder().createDatetime(createdNow).updateDatetime(updateNow).build())
		.commonFlag(CommonFlagEntity.builder().deleteFlag(true).build())
		.build();
	}
	
	@SuppressWarnings("unused")
	private static Stream<Arguments> save() throws IOException{
		return Stream.of(
				// normal scenario
				Arguments.of(
						// inserted
						insertedData_normal_case()
						,
						// Expect
						ProfileEntity.builder()
						.ImageBase64(getBase64()).ImageType(DEFAULT_IMAGE_EXTENTION)
						.commonDate(CommonDateEntity.builder().createDatetime(createdNow).updateDatetime(updateNow).build())
						.commonFlag(CommonFlagEntity.builder().deleteFlag(true).build())
						.build())
		);		
	}
}
