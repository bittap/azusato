package com.my.azusato.integration;

import java.io.File;
import java.nio.file.Paths;
import java.util.Arrays;

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
import com.my.azusato.property.CelebrationProperty;
import com.my.azusato.property.ProfileProperty;
import com.my.azusato.repository.CelebrationNoticeRepository;
import com.my.azusato.repository.CelebrationReplyNoticeRepository;
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
public abstract class AbstractIntegration  {
	
	@Autowired
	protected MockMvc mockMvc;
	
	protected ObjectMapper om = new ObjectMapper();
	
	@Autowired
	protected ProfileProperty profileProperty;
	
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
	protected CelebrationRepository celeRepo;
	
	@Autowired
	protected CelebrationReplyRepository celeReplyRepo;
	
	@Autowired
	protected CelebrationNoticeRepository celeNoticeRepo;
	
	@Autowired
	protected CelebrationReplyNoticeRepository celeReplyNoticeRepo;
	
	@Autowired
	protected CelebrationProperty celeProperty;
	
	public AbstractIntegration() {
		// ????????????javatime
		om.registerModule(new JavaTimeModule());
	}
	
	
	/**
	 * delete all data. commit for deleting all data and then start to commit for avoiding lazy exception. Because session is closed 
	 * Reference this <a href="https://stackoverflow.com/questions/11746499/how-to-solve-the-failed-to-lazily-initialize-a-collection-of-role-hibernate-ex.">lazily-initialize</a> 
	 *  ?????????????????????????????????????????????????????????????????????
	 */
	@BeforeEach
	@Commit
	public void superBeforeEach() {
		allDataDelete();
		allFileDelete();
	}
	
	private void allDataDelete() {
		profileRepo.deleteAll();
		celeReplyRepo.deleteAll();
		celeRepo.deleteAll();
		userRepo.deleteAll();
	}
	
	/**
	 * ????????????????????????????????????????????????????????????
	 */
	private void allFileDelete() {
		File file1 = Paths.get(celeProperty.getServerContentFolderPath()).toFile();
		Arrays.asList(file1.listFiles()).forEach(File::delete);
		
		File file2 = Paths.get(profileProperty.getClientImageFolderPath()).toFile();
		Arrays.asList(file2.listFiles()).forEach(File::delete);
	}
	
	protected void commitAndStart() {
		// commit to delete all data  
		TestTransaction.end();
		// start transaction.
		TestTransaction.start();
	}
	
}
