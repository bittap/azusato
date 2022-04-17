package com.my.azusato.integration.entity;

import java.nio.file.Paths;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.my.azusato.common.TestConstant;
import com.my.azusato.common.TestUtils;
import com.my.azusato.dbunit.DBUnitComponent;
import com.my.azusato.entity.CelebrationEntity;
import com.my.azusato.entity.CelebrationReplyEntity;
import com.my.azusato.entity.UserEntity;
import com.my.azusato.entity.common.CommonDateEntity;
import com.my.azusato.entity.common.CommonFlagEntity;
import com.my.azusato.entity.common.CommonUserEntity;
import com.my.azusato.integration.AbstractIntegration;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CelebrationReplyTest extends AbstractIntegration {
	
	@Autowired
	DBUnitComponent dbunitCompo;

	public final String CURRENT_FOLDER =  TestConstant.COMMON_ENTITY_FOLDER+"celebration_reply";
	
	@Nested
	class Save{
		
		private long insertedCelebrationNO = 1L;
		
		@BeforeEach
		public void beforeEach() throws Exception {
			dbunitCompo.initalizeTable(Paths.get(CURRENT_FOLDER,"init","save.xml"));
		}
		
		@Test
		public void normal_case() throws Exception {	
			CelebrationReplyEntity celebrationReplyEntity = CelebrationReplyEntity.builder()
									.celebrationNo(insertedCelebrationNO)
									.content(TestConstant.Entity.createdVarChars[0])
									.commonUser(CommonUserEntity.builder().createUserEntity(expectedUserEntity()).updateUserEntity(expectedUserEntity()).build())
									.commonDate(CommonDateEntity.builder().createDatetime(TestConstant.Entity.createdDatetime).updateDatetime(TestConstant.Entity.updatedDatetimeWhenCreate).build())
									.commonFlag(CommonFlagEntity.builder().deleteFlag(true).build())
									.build();
			
			celeReplyRepo.save(celebrationReplyEntity);
			// commit and transaction start
			commitAndStart();
			
			List<CelebrationEntity> results = celeRepo.findAll();
			CelebrationEntity result = TestUtils.getLastElement(results);
			
			Assertions.assertEquals(getExpect(insertedCelebrationNO), result);
		}
		
		private UserEntity expectedUserEntity() {
			return userRepo.findAll().get(TestConstant.Entity.GET_INDEXS[0]);
		}
		
		private List<CelebrationReplyEntity> getExpectReplys(long celebrationNo){
			CelebrationReplyEntity celebrationReplyEntity = CelebrationReplyEntity.builder()
					.celebrationNo(insertedCelebrationNO)
					.content(TestConstant.Entity.createdVarChars[0])
					.commonUser(CommonUserEntity.builder().createUserEntity(expectedUserEntity()).updateUserEntity(expectedUserEntity()).build())
					.commonDate(CommonDateEntity.builder().createDatetime(TestConstant.Entity.createdDatetime).updateDatetime(TestConstant.Entity.updatedDatetimeWhenCreate).build())
					.commonFlag(CommonFlagEntity.builder().deleteFlag(true).build())
					.replyNotices(Set.of())
					.build();
			
			return List.of(celebrationReplyEntity);
		}
		
		private CelebrationEntity getExpect(long celebrationNo) {
			return CelebrationEntity.builder()
					.no(celebrationNo)
					.title(TestConstant.Entity.createdVarChars[0])
					.content(TestConstant.Entity.createdVarChars[1])
					.commonUser(CommonUserEntity.builder().createUserEntity(expectedUserEntity()).updateUserEntity(expectedUserEntity()).build())
					.readCount(TestConstant.Entity.createdInts[0])
					.commonDate(CommonDateEntity.builder().createDatetime(TestConstant.Entity.createdDatetime).updateDatetime(TestConstant.Entity.updatedDatetimeWhenCreate).build())
					.commonFlag(CommonFlagEntity.builder().deleteFlag(true).build())
					.replys(getExpectReplys(celebrationNo))
					.notices(Set.of())
					.build();
		}
		
		
	}
}
