package com.my.azusato.integration.entity;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.my.azusato.common.TestConstant;
import com.my.azusato.dbunit.DBUnitComponent;
import com.my.azusato.entity.CelebrationEntity;
import com.my.azusato.entity.CelebrationNoticeEntity;
import com.my.azusato.entity.UserEntity;
import com.my.azusato.entity.UserEntity.Type;
import com.my.azusato.integration.AbstractIntegration;
import com.my.azusato.repository.CelebrationNoticeRepository;
import com.my.azusato.repository.CelebrationRepository;
import com.my.azusato.repository.UserRepository;

public class CelebrationNoticeEntityTest extends AbstractIntegration {
	
	@Autowired
	DBUnitComponent dbunitCompo;
	
	@Autowired
	CelebrationRepository celebrationRepo;
	
	@Autowired
	UserRepository userRepo;
	
	@Autowired
	CelebrationNoticeRepository celebrationNoticeRepo;

	@BeforeEach
	public void beforeEach() throws Exception {
		dbunitCompo.initalizeTable(Paths.get(TestConstant.COMMON_ENTITY_FOLDER,"celebrationNotice.xml"));
	}
	
	@Nested
	class Save{
		
		@Test
		public void normal_case() {
			List<UserEntity> admins = userRepo.findByUserType(Type.admin.toString());
			CelebrationEntity celeEntity = celebrationRepo.findAll().get(TestConstant.Entity.GET_INDEXS[0]);
			
			List<CelebrationNoticeEntity> results = new ArrayList<>();
			for (UserEntity admin : admins) {
				CelebrationNoticeEntity noticeEntity = new CelebrationNoticeEntity(celeEntity, admin);
				CelebrationNoticeEntity savedEntity = celebrationNoticeRepo.save(noticeEntity);
				results.add(savedEntity);
			}

			Assertions.assertEquals(getExpcet(), results);
		}
	}
	
	private List<CelebrationNoticeEntity> getExpcet(){
		CelebrationEntity celeEntity = celebrationRepo.findAll().get(TestConstant.Entity.GET_INDEXS[0]);
		List<UserEntity> admins = userRepo.findByUserType(Type.admin.toString());
		
		List<CelebrationNoticeEntity> expect = List.of(
				new CelebrationNoticeEntity(celeEntity, admins.get(0)),
				new CelebrationNoticeEntity(celeEntity, admins.get(1))
			);
				
		return expect;
	}
	
	@AfterEach
	public void afterEach() throws Exception {
		dbunitCompo.deleteTable();
	}
}
