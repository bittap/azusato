package com.my.azusato.integration.interceptor;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.servlet.ModelAndView;

import com.my.azusato.api.service.response.GetCelebrationNoticesSerivceAPIResponse;
import com.my.azusato.common.TestConstant;
import com.my.azusato.common.TestLogin;
import com.my.azusato.integration.AbstractIntegration;

public class NavigationInterceptorTest extends AbstractIntegration {
	
	private final String NO_EXIST_URL = "test";
	
	final static String RESOUCE_BASIC_PATH = "src/test/data/integration/interceptor/NavigationInterceptor/";

	@Nested
	class postHandle{
		
		final static String RESOUCE_PATH = RESOUCE_BASIC_PATH + "postHandle/";
		
		final String NAV_MODEL_KEY = "nav";
		
		@Test
		@DisplayName("正常系_404ページでモデルがない_結果modelはnull")
		void givenNoModel_resultModelIsNull() throws Exception{
			MvcResult resultOfMvc = mockMvc.perform(MockMvcRequestBuilders.get(TestConstant.MAKE_ABSOLUTE_URL + NO_EXIST_URL))
			.andDo(print())
			.andReturn();
			
			ModelAndView mavOfResult = resultOfMvc.getModelAndView();

			Assertions.assertNull(mavOfResult);
		}
	
		@Test
		@DisplayName("正常系_ログインしていない_結果キーnavModelはnull")
		void givenNoLogin_resultModelNavIsNull() throws Exception{
			MvcResult resultOfMvc = mockMvc.perform(MockMvcRequestBuilders.get(TestConstant.MAKE_ABSOLUTE_URL))
			.andDo(print())
			.andReturn();
			
			ModelAndView mavOfResult = resultOfMvc.getModelAndView();

			Map<String, Object> mapsOfResult = mavOfResult.getModel();
			
			Assertions.assertNull(mapsOfResult.get(NAV_MODEL_KEY));
		}
		
		@Test
		@DisplayName("正常系_ログインしていてお祝い通知2件ある_結果キーnavModelの結果2件")
		void givenLoginAndCelebrationNotice2data_resultModelNavIs2data() throws Exception{
			String folderName = "1";
			Path initFilePath = Paths.get(RESOUCE_PATH, folderName, TestConstant.INIT_XML_FILE_NAME);
			dbUnitCompo.initalizeTable(initFilePath);
			
			MvcResult resultOfMvc = mockMvc.perform(
					MockMvcRequestBuilders.get(TestConstant.MAKE_ABSOLUTE_URL)
					.with(user(TestLogin.adminLoginUser())))
			.andDo(print())
			.andReturn();
			
			ModelAndView mavOfResult = resultOfMvc.getModelAndView();

			Map<String, Object> mapsOfResult = mavOfResult.getModel();
			
			Assertions.assertNotNull(mapsOfResult.get(NAV_MODEL_KEY));
			
			GetCelebrationNoticesSerivceAPIResponse result = (GetCelebrationNoticesSerivceAPIResponse)mapsOfResult.get(NAV_MODEL_KEY);
			Assertions.assertEquals(2,result.getNotices().size());
		}
	}
}
