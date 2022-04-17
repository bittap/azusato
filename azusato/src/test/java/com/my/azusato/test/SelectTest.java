package com.my.azusato.test;

import java.util.List;

import javax.transaction.Transactional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.my.azusato.common.TestConstant;
import com.my.azusato.entity.CelebrationEntity;
import com.my.azusato.repository.CelebrationRepository;

@SpringBootTest
@ActiveProfiles(TestConstant.PROFILES)
public class SelectTest {
	
	@Autowired
	CelebrationRepository celeRepo;

	@Test
	@Transactional
	public void test() {
		List<CelebrationEntity> celes = celeRepo.findAll();
		System.out.println(celes);
	}
}
