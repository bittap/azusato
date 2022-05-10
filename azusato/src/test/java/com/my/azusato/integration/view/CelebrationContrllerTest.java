package com.my.azusato.integration.view;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.file.Paths;
import java.util.Map;
import java.util.Objects;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.servlet.ModelAndView;

import com.my.azusato.common.TestConstant;
import com.my.azusato.common.TestConstant.Entity;
import com.my.azusato.common.TestSession;
import com.my.azusato.integration.AbstractIntegration;
import com.my.azusato.property.ProfileProperty;
import com.my.azusato.view.controller.common.ModelConstant;
import com.my.azusato.view.controller.common.UrlConstant;
import com.my.azusato.view.controller.common.UrlConstant.Api;
import com.my.azusato.view.controller.response.CelebrationWriteResponse;
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
		 * セッションがある場合、そのセッション情報のユーザデータを返す。
		 * @throws Exception
		 */
		@Test
		public void givenSession_resultUserInfo() throws Exception{
			dbUnitCompo.initalizeTable(Paths.get(RESOUCE_PATH, "1", TestConstant.INIT_XML_FILE_NAME));
			MvcResult mvcResult = mockMvc.perform(
						MockMvcRequestBuilders.get( UrlConstant.CELEBRATION_CONTROLLER_REQUSET + Api.CELEBRATION_WRITE_URL).session(TestSession.getAdminSession())
						)
					.andDo(print())
					.andExpect(status().isOk()).andReturn();
			
			ModelAndView mavResult = mvcResult.getModelAndView();
			
			Assertions.assertEquals("celebration/write", mavResult.getViewName());
			
			Map<String, Object> mapsOfResult = mavResult.getModel();
			
			compareHeader(mapsOfResult);
			
			CelebrationWriteResponse resultWr = (CelebrationWriteResponse)mapsOfResult.get(ModelConstant.DATA_KEY);
			CelebrationWriteResponse expectWr = CelebrationWriteResponse.builder()
									.name(Entity.createdVarChars[2])
									.ImageBase64(Entity.createdVarChars[0])
									.ImageType(Entity.ImageType[0])
									.build();
			
			Assertions.assertEquals(expectWr, resultWr);
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
			
			CelebrationWriteResponse resultWr = (CelebrationWriteResponse)mapsOfResult.get(ModelConstant.DATA_KEY);

			Assertions.assertTrue(Objects.isNull(resultWr.getName()));
			Assertions.assertTrue(Objects.nonNull(resultWr.getImageBase64()));
			Assertions.assertEquals(profileProperty.getDefaultImageType(), resultWr.getImageType());
		}
		
		private void compareHeader(Map<String, Object> mapsOfResult) {
			HeaderReponse resultHr = (HeaderReponse)mapsOfResult.get(ModelConstant.HEADER_KEY);
			HeaderReponse expectHr = new HeaderReponse();
			expectHr.setCelebration(true);
			
			Assertions.assertEquals(expectHr, resultHr);
		}
	}
}
