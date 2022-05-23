package com.my.azusato.integration.entity;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.my.azusato.common.TestConstant;
import com.my.azusato.common.TestUtils;
import com.my.azusato.entity.CelebrationContentEntity;
import com.my.azusato.entity.UserEntity;
import com.my.azusato.entity.common.CommonDateEntity;
import com.my.azusato.entity.common.CommonFlagEntity;
import com.my.azusato.entity.common.CommonUserEntity;
import com.my.azusato.integration.AbstractIntegration;

public class CelebrationEntityTest extends AbstractIntegration {

	@BeforeEach
	public void beforeEach() throws Exception {
		dbUnitCompo.initalizeTable(Paths.get(TestConstant.COMMON_ENTITY_FOLDER,"celebration.xml"));
	}
	
	@Nested
	class Save{
		
		@Test
		public void normal_case() throws Exception { 
			celeRepo.save(insertedData_normal_case());
			
			List<CelebrationContentEntity> results = celeRepo.findAll();
			
			CelebrationContentEntity result = TestUtils.getLastElement(results);
			CelebrationContentEntity expect = CelebrationContentEntity.builder()
										.no(result.getNo())
										.title(TestConstant.Entity.createdVarChars[0])
										.content(getContent())
										.commonUser(CommonUserEntity.builder().createUserEntity(expectedUserEntity()).updateUserEntity(expectedUserEntity()).build())
										.readCount(TestConstant.Entity.createdInts[0])
										.commonDate(CommonDateEntity.builder().createDatetime(TestConstant.Entity.createdDatetime).updateDatetime(TestConstant.Entity.updatedDatetime).build())
										.commonFlag(CommonFlagEntity.builder().deleteFlag(true).build())
										.build();
			
			Assertions.assertEquals(expect, result);
		}
	}
	
	@Nested
	class Update{
		
		private final LocalDateTime updatedNowForTest = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);

		@Test
		public void normal_case() throws Exception {
			celeRepo.save(insertedData_normal_case());
					
			List<CelebrationContentEntity> results = celeRepo.findAll();
			CelebrationContentEntity savedData = TestUtils.getLastElement(results);
			
			savedData.setTitle(TestConstant.Entity.updatedVarChars[0]);
			savedData.setContent(TestConstant.Entity.updatedVarChars[1]);
			savedData.setReadCount(TestConstant.Entity.updatedInts[0]);
			savedData.setCommonDate(CommonDateEntity.builder().createDatetime(TestConstant.Entity.updatedDatetime).updateDatetime(updatedNowForTest).build());
			savedData.setCommonFlag(new CommonFlagEntity(TestConstant.Entity.UpdatedBoolean));
			
			CelebrationContentEntity result = celeRepo.save(savedData);
			
			Assertions.assertEquals(getExpect(savedData.getNo(),savedData.getCommonUser()), result);
		}
		
		private CelebrationContentEntity getExpect(long celebrationNo, CommonUserEntity commonUserEntity) {
			CelebrationContentEntity expectedData = new CelebrationContentEntity();
			expectedData.setNo(celebrationNo);
			expectedData.setTitle(TestConstant.Entity.updatedVarChars[0]);
			expectedData.setContent(TestConstant.Entity.updatedVarChars[1]);
			expectedData.setReadCount(TestConstant.Entity.updatedInts[0]);
			expectedData.setCommonUser(commonUserEntity);
			expectedData.setCommonDate(CommonDateEntity.builder().createDatetime(TestConstant.Entity.updatedDatetime).updateDatetime(updatedNowForTest).build());
			expectedData.setCommonFlag(new CommonFlagEntity(TestConstant.Entity.UpdatedBoolean));
			
			return expectedData;
		}
	}
	
	/**
	 * return inserted entity which is normal case.
	 */
	private CelebrationContentEntity insertedData_normal_case() throws Exception {
		return CelebrationContentEntity.builder()
				.title(TestConstant.Entity.createdVarChars[0])
				.content(getContent())
				.readCount(TestConstant.Entity.createdInts[0])
				.commonUser(CommonUserEntity.builder().createUserEntity(expectedUserEntity()).updateUserEntity(expectedUserEntity()).build())
				.commonDate(CommonDateEntity.builder().createDatetime(TestConstant.Entity.createdDatetime).updateDatetime(TestConstant.Entity.updatedDatetime).build())
				.commonFlag(CommonFlagEntity.builder().deleteFlag(true).build())
				.build();
	}
	
	private UserEntity expectedUserEntity() {		UserEntity userEntity = userRepo.findAll().get(TestConstant.Entity.GET_INDEXS[0]);
		userEntity.setPassword("aaaaaaaaaaaaaaaaaaa");
		return userEntity;
	}
	
	private String getContent() throws Exception {
		Path path = Paths.get(TestConstant.COMMON_TEST_DATA_FOLDER,"celebrationBody.txt");
		return Files.lines(path).collect(Collectors.joining());
	}
}
