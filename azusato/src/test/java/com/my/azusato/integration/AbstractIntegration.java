package com.my.azusato.integration;

import javax.sql.DataSource;

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
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.my.azusato.common.TestConstant;
import com.my.azusato.dbunit.DBUnitComponent;
import com.my.azusato.login.Grant;
import com.my.azusato.repository.CelebrationContentRepository;
import com.my.azusato.repository.CelebrationNoticeRepository;
import com.my.azusato.repository.CelebrationReplyNoticeRepository;
import com.my.azusato.repository.CelebrationReplyRepository;
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
public abstract class AbstractIntegration  {
	
	@Autowired
	protected MockMvc mockMvc;
	
	@Autowired
	protected Grant grant;
	
	protected ObjectMapper om = new ObjectMapper();
	
	@Autowired
	protected DataSource dataSource;
	
	@Autowired
	protected DBUnitComponent dbUnitCompo;
	
	@Autowired
	protected MessageSource messageSource;
	
	@Autowired
	protected ProfileRepository profileRepo;
	
	@Autowired
	protected UserRepository userRepo;
	
	@Autowired
	protected CelebrationContentRepository celeRepo;
	
	@Autowired
	protected CelebrationReplyRepository celeReplyRepo;
	
	@Autowired
	protected CelebrationNoticeRepository celeNoticeRepo;
	
	@Autowired
	protected CelebrationReplyNoticeRepository celeReplyNoticeRepo;
	
	public AbstractIntegration() {
		// サポートjavatime
		om.registerModule(new JavaTimeModule());
	}
	
	
	/**
	 * delete all data. commit for deleting all data and then start to commit for avoiding lazy exception. Because session is closed 
	 * Reference this <a href="https://stackoverflow.com/questions/11746499/how-to-solve-the-failed-to-lazily-initialize-a-collection-of-role-hibernate-ex.">lazily-initialize</a>  
	 */
	@BeforeEach
	@Commit
	public void allDataDelete() {
		profileRepo.deleteAll();
		celeReplyRepo.deleteAll();
		celeRepo.deleteAll();
		userRepo.deleteAll();
	}
	
	protected void commitAndStart() {
		// commit to delete all data  
		TestTransaction.end();
		// start transaction.
		TestTransaction.start();
	}
	
}
