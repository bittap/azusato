package com.my.azusato.integration.entity;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;

import com.my.azusato.common.TestConstant;
import com.my.azusato.common.TestUtils;
import com.my.azusato.dbunit.DBUnitComponent;
import com.my.azusato.entity.CelebrationEntity;
import com.my.azusato.entity.CelebrationNoticeEntity;
import com.my.azusato.entity.UserEntity;
import com.my.azusato.entity.UserEntity.Type;
import com.my.azusato.entity.common.CommonDateEntity;
import com.my.azusato.entity.common.CommonFlagEntity;
import com.my.azusato.entity.common.CommonUserEntity;
import com.my.azusato.integration.AbstractIntegration;
import com.my.azusato.repository.CelebrationNoticeRepository;
import com.my.azusato.repository.CelebrationRepository;
import com.my.azusato.repository.ProfileRepository;
import com.my.azusato.repository.UserRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CelebrationNoticeTest extends AbstractIntegration {
	
	@Autowired
	CelebrationRepository celeRepo;
	
	@Autowired
	UserRepository userRepo;
	
	@Autowired
	DBUnitComponent dbunitCompo;
	
	@Autowired
	ProfileRepository profileRepo;
	
	@Autowired
	CelebrationNoticeRepository celeNoticeRepo;
	
	
	public final String CURRENT_FOLDER =  TestConstant.COMMON_ENTITY_FOLDER+"celebration_notice";
	
	@Nested
	class Save{
		
		@BeforeEach
		public void beforeEach() throws Exception {
			dbunitCompo.initalizeTable(Paths.get(CURRENT_FOLDER,"init","save.xml"));
		}
		
		@Test
		public void normal_case() throws Exception {
			celeRepo.save(insertedData_normal_case());

			List<CelebrationEntity> results = celeRepo.findAll();
			
			CelebrationEntity result = TestUtils.getLastElement(results);
			
			CelebrationEntity expect = CelebrationEntity.builder()
										.no(result.getNo())
										.title(TestConstant.Entity.createdVarChars[0])
										.content(getContent())
										.commonUser(CommonUserEntity.builder().createUserEntity(expectedUserEntity()).updateUserEntity(expectedUserEntity()).build())
										.readCount(TestConstant.Entity.createdInts[0])
										.commonDate(CommonDateEntity.builder().createDatetime(TestConstant.Entity.createdDatetime).updateDatetime(TestConstant.Entity.updatedDatetime).build())
										.commonFlag(CommonFlagEntity.builder().deleteFlag(true).build())
										.notices(getNotices())
										.build();
			
			Assertions.assertEquals(expect, result);
		}
		
		private Set<UserEntity> getNotices(){
			return userRepo.findByUserType(Type.admin.toString());
		}

		/**
		 * return inserted entity which is normal case.
		 */
		private CelebrationEntity insertedData_normal_case() throws Exception {
			return CelebrationEntity.builder()
					.title(TestConstant.Entity.createdVarChars[0])
					.content(getContent())
					.readCount(TestConstant.Entity.createdInts[0])
					.commonUser(CommonUserEntity.builder().createUserEntity(expectedUserEntity()).updateUserEntity(expectedUserEntity()).build())
					.commonDate(CommonDateEntity.builder().createDatetime(TestConstant.Entity.createdDatetime).updateDatetime(TestConstant.Entity.updatedDatetime).build())
					.commonFlag(CommonFlagEntity.builder().deleteFlag(true).build())
					.notices(getNotices())
					.build();
		}
		
		private UserEntity expectedUserEntity() {
			return userRepo.findAll().get(TestConstant.Entity.GET_INDEXS[0]);
		}
		
		private String getContent() throws Exception {
			Path path = Paths.get(TestConstant.COMMON_TEST_DATA_FOLDER,"celebrationBody.txt");
			return Files.lines(path).collect(Collectors.joining());
		}
	}
	
	@Nested
	class Delete{
		
		@BeforeEach
		public void beforeEach() throws Exception {
			dbunitCompo.initalizeTable(Paths.get(CURRENT_FOLDER,"init","delete.xml"));
		}
		
		@Test
		public void normal_case() throws Exception {
			List<CelebrationNoticeEntity> notices = celeNoticeRepo.findAll();
			// check notice entity's count
			Assertions.assertEquals(2, notices.size());
			celeNoticeRepo.delete(notices.get(0));		
			
			Assertions.assertEquals(getExpectCelebrationNotice2Index(),celeNoticeRepo.findAll().get(0));
		}
		
		private CelebrationNoticeEntity getExpectCelebrationNotice2Index() throws Exception {
			return new CelebrationNoticeEntity(1L, 2L);
		}
		
	}
	
	@Nested
	class Select{
		
		@BeforeEach
		public void beforeEach() throws Exception {
			dbunitCompo.initalizeTable(Paths.get(CURRENT_FOLDER,"init","select.xml"));
		}
		
		@ParameterizedTest
		@MethodSource("com.my.azusato.integration.entity.CelebrationNoticeTest#celebrations")
		public void normal_case(long selectedUserNo, int expectdSize) throws Exception {
			List<CelebrationNoticeEntity> notices =celeNoticeRepo.findAllByUserNo(selectedUserNo);
			Assertions.assertEquals(expectdSize, notices.size());

			List<Long> celebrationNos = notices.stream().map(CelebrationNoticeEntity::getCelebrationNo).collect(Collectors.toList());
			
			List<CelebrationEntity> sizeByUser = celeRepo.findAllByNoIn(celebrationNos);
			Assertions.assertEquals(expectdSize, sizeByUser.size());
		}
		
		
	}
	
	
	public static Stream<Arguments> celebrations(){
		return Stream.of(
					Arguments.of(1L,1),
					Arguments.of(2L,2)
				);
				
	}
}
