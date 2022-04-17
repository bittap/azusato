package com.my.azusato.integration;

import javax.transaction.Transactional;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.transaction.TestTransaction;

import com.my.azusato.common.TestConstant;
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
	protected ProfileRepository profileRepo;
	
	@Autowired
	protected UserRepository userRepo;
	
	@Autowired
	protected CelebrationRepository celeRepo;
	
	/**
	 * delete all data. commit for deleting all data and then start to commit for avoiding lazy exception. Because session is closed 
	 * Reference this <a href="https://stackoverflow.com/questions/11746499/how-to-solve-the-failed-to-lazily-initialize-a-collection-of-role-hibernate-ex.">lazily-initialize</a>  
	 */
	@BeforeEach
	public void allDataDelete() {
		profileRepo.deleteAll();
		celeRepo.deleteAll();
		userRepo.deleteAll();
		
		// commit to delete all data  
		TestTransaction.end();
		// start transaction.
		TestTransaction.start();
	}
	
}
