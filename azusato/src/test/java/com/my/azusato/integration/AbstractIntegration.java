package com.my.azusato.integration;

import javax.transaction.Transactional;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.my.azusato.common.TestConstant;

/**
 * for integration test
 * @author Carmel
 *
 */
@SpringBootTest
@ActiveProfiles(TestConstant.PROFILES)
@Transactional
@AutoConfigureMockMvc
public abstract class AbstractIntegration  {
}
