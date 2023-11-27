package com.my.azusato.integration;

import java.io.File;
import java.nio.file.Paths;
import java.util.Arrays;
import javax.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.MessageSource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.my.azusato.common.TestConstant;
import com.my.azusato.common.TestConstant.Entity;
import com.my.azusato.entity.UserEntity;
import com.my.azusato.property.CelebrationProperty;
import com.my.azusato.property.ProfileProperty;
import com.my.azusato.repository.ProfileRepository;
import com.my.azusato.repository.UserRepository;

/**
 * for integration test
 * 
 * @author Carmel
 *
 */
@SpringBootTest
@Transactional
@ActiveProfiles(TestConstant.PROFILES)
@AutoConfigureMockMvc
public abstract class AbstractIntegrationForTest {

  @Autowired
  protected MockMvc mockMvc;

  protected ObjectMapper om = new ObjectMapper();

  @Autowired
  protected ProfileProperty profileProperty;

  @Autowired
  protected MessageSource messageSource;

  @Autowired
  protected CelebrationProperty celeProperty;

  @Autowired
  protected UserRepository userRepo;

  @Autowired
  protected ProfileRepository profileRepo;

  protected UserEntity adminUser;

  public AbstractIntegrationForTest() {
    // サポートjavatime
    om.registerModule(new JavaTimeModule());
  }

  /**
   * テストフォルダにあるファイル全部削除する。
   */
  @BeforeEach
  public void deleteAllFileInTestFolder() {
    File file1 = Paths.get(celeProperty.getServerContentFolderPath()).toFile();
    Arrays.asList(file1.listFiles()).forEach(File::delete);

    File file2 = Paths.get(profileProperty.getClientImageFolderPath()).toFile();
    Arrays.asList(file2.listFiles()).forEach(File::delete);
  }

  protected UserEntity getAdminUser() {
    return userRepo.findById(Entity.ADMIN_USER_NOS[0]).get();
  }

  protected UserEntity getNonMember() {
    return userRepo.findById(Entity.NONMEMBER_USER_NOS[0]).get();
  }

}
