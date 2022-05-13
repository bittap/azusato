package com.my.azusato.integration.view;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.servlet.ModelAndView;

import com.my.azusato.integration.AbstractIntegration;
import com.my.azusato.property.ProfileProperty;
import com.my.azusato.view.controller.common.ModelConstant;
import com.my.azusato.view.controller.common.UrlConstant;
import com.my.azusato.view.controller.common.UrlConstant.Api;
import com.my.azusato.view.controller.response.HeaderReponse;

public class CelebrationContrllerTest extends AbstractIntegration {

	final static String RESOUCE_BASIC_PATH = "src/test/data/integration/view/controller/";
	
	@Autowired
	ProfileProperty profileProperty;
	
	@Nested
	public class Write {
		
		final static String RESOUCE_PATH = RESOUCE_BASIC_PATH + "write/";

		@ParameterizedTest
		@ValueSource(strings = { UrlConstant.CELEBRATION_CONTROLLER_REQUSET + Api.CELEBRATION_WRITE_URL, 
				UrlConstant.JAPANESE_CONTROLLER_REQUEST + UrlConstant.CELEBRATION_CONTROLLER_REQUSET + Api.CELEBRATION_WRITE_URL,
				UrlConstant.KOREAN_CONTROLLER_REQUEST + UrlConstant.CELEBRATION_CONTROLLER_REQUSET + Api.CELEBRATION_WRITE_URL })
		public void givenUrl_resultNot404(String url) throws Exception {
			mockMvc.perform(MockMvcRequestBuilders.get(url)).andExpect(status().is(200));
		}

		/**
		 * @throws Exception
		 */
		@Test
		public void givenNoSession_resultOk() throws Exception{
			MvcResult mvcResult = mockMvc.perform(
						MockMvcRequestBuilders.get(UrlConstant.CELEBRATION_CONTROLLER_REQUSET + Api.CELEBRATION_WRITE_URL)
						)
					.andDo(print())
					.andExpect(status().isOk()).andReturn();
			
			ModelAndView mavResult = mvcResult.getModelAndView();
			
			Map<String, Object> mapsOfResult = mavResult.getModel();
			
			compareHeader(mapsOfResult);
		}
		
		private void compareHeader(Map<String, Object> mapsOfResult) {
			HeaderReponse resultHr = (HeaderReponse)mapsOfResult.get(ModelConstant.HEADER_KEY);
			HeaderReponse expectHr = new HeaderReponse();
			expectHr.setCelebration(true);
			
			Assertions.assertEquals(expectHr, resultHr);
		}
	}
}
