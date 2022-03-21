package com.my.azusato.integration.view;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.servlet.ModelAndView;

import com.my.azusato.common.TestConstant;
import com.my.azusato.view.controller.common.ModelConstant;
import com.my.azusato.view.controller.common.UrlConstant;
import com.my.azusato.view.controller.response.HeaderReponse;

@SpringBootTest
@AutoConfigureMockMvc
public class IndexControllerTest {

	@Autowired
	MockMvc mockMvc;
	
	private final int MODEL_SIZE = 1 + TestConstant.SPRING_MODEL_SIZE;
	
	@Nested
	public class Status_200{
		
		/**
		 * Check 200 status,model size and model values
		 */
		@ParameterizedTest
		@ValueSource(
					strings = {UrlConstant.INDEX_CONTROLLER_REQUSET,
					UrlConstant.INDEX_CONTROLLER_REQUSET+UrlConstant.JAPANESE_CONTROLLER_REQUEST,
					UrlConstant.INDEX_CONTROLLER_REQUSET+UrlConstant.KOREAN_CONTROLLER_REQUEST}
				)
		public void givenPath_mapped(String url) throws Exception {
			MvcResult resultOfMvc =  mockMvc.perform(
					MockMvcRequestBuilders
						.get(url)
					)
					.andExpect(status().is(200))
					.andReturn();
			
			ModelAndView mavOfResult = resultOfMvc.getModelAndView();
			
			Map<String, Object> mapsOfResult = mavOfResult.getModel();
			mapsOfResult.forEach((k,v)->{
				System.out.printf("key : %s, value : %s\n",k,v);
			});
			
			Assertions.assertEquals(MODEL_SIZE, mapsOfResult.size());

			
			HeaderReponse headerOfResult = (HeaderReponse)mapsOfResult.get(ModelConstant.HEADER_KEY);
			
			HeaderReponse expect = new HeaderReponse();
			expect.setHome(true);
			
			Assertions.assertEquals(expect, headerOfResult);
		}
	}
}
