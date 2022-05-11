package com.my.azusato.integration.api.controller;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.Charset;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.my.azusato.common.TestConstant;
import com.my.azusato.common.TestSession;
import com.my.azusato.integration.AbstractIntegration;
import com.my.azusato.view.controller.common.UrlConstant.Api;

public class SessionContollerAPITest extends AbstractIntegration {
	

	@Nested
	class CheckExistSession {

		@Test
		public void givenSession_resultTrue() throws Exception {
			MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders
						.get(TestConstant.MAKE_ABSOLUTE_URL + Api.COMMON_REQUSET + Api.SESSION_CONTROLLER_REQUSET + Api.CHECK_EXIST_SESSION_URL).session(TestSession.getAdminSession())
					).andDo(print()).andExpect(status().isOk()).andReturn();
			
			String result = mvcResult.getResponse().getContentAsString(Charset.forName(TestConstant.DEFAULT_CHARSET));
			
			Assertions.assertEquals("true", result);
		}
		
		@Test
		public void givenNoSession_resultFalse() throws Exception {
			MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders
						.get(TestConstant.MAKE_ABSOLUTE_URL + Api.COMMON_REQUSET + Api.SESSION_CONTROLLER_REQUSET + Api.CHECK_EXIST_SESSION_URL)
					).andDo(print()).andExpect(status().isOk()).andReturn();
			
			String result = mvcResult.getResponse().getContentAsString(Charset.forName(TestConstant.DEFAULT_CHARSET));
			
			Assertions.assertEquals("false", result);
		}
	}
}
