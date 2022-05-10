package com.my.azusato.integration;

import javax.transaction.Transactional;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.MessageSource;
import org.springframework.test.annotation.Commit;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.transaction.TestTransaction;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.my.azusato.common.TestConstant;
import com.my.azusato.dbunit.DBUnitComponent;
import com.my.azusato.repository.CelebrationReplyRepository;
import com.my.azusato.repository.CelebrationRepository;
import com.my.azusato.repository.ProfileRepository;
import com.my.azusato.repository.UserRepository;

/**
 * for integration test
 * @author Carmel
 *
 */
@SpringBootTest
@ActiveProfiles(TestConstant.PROFILES)
@AutoConfigureMockMvc
@Transactional
@Commit
public abstract class AbstractIntegration  {
	
	@Autowired
	protected MockMvc mockMvc;
	
	protected ObjectMapper om = new ObjectMapper();
	
	@Autowired
	protected DBUnitComponent dbUnitCompo;
	
	@Autowired
	protected MessageSource messageSource;
	
	@Autowired
	protected ProfileRepository profileRepo;
	
	@Autowired
	protected UserRepository userRepo;
	
	@Autowired
	protected CelebrationRepository celeRepo;
	
	@Autowired
	protected CelebrationReplyRepository celeReplyRepo;
	
	/**
	 * delete all data. commit for deleting all data and then start to commit for avoiding lazy exception. Because session is closed 
	 * Reference this <a href="https://stackoverflow.com/questions/11746499/how-to-solve-the-failed-to-lazily-initialize-a-collection-of-role-hibernate-ex.">lazily-initialize</a>  
	 */
	@BeforeEach
	public void allDataDelete() {
		profileRepo.deleteAll();
		celeReplyRepo.deleteAll();
		celeRepo.deleteAll();
		userRepo.deleteAll();
		
		commitAndStart();
	}
	
	protected void commitAndStart() {
		// commit to delete all data  
		TestTransaction.end();
		// start transaction.
		TestTransaction.start();
	}
	
}
