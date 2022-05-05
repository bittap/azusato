package com.my.azusato.integration.api.controller;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.Charset;
import java.util.Locale;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.my.azusato.api.controller.request.AddNonMemberUserAPIRequest;
import com.my.azusato.common.TestConstant;
import com.my.azusato.common.TestConstant.Entity;
import com.my.azusato.common.TestCookie;
import com.my.azusato.exception.AzusatoException;
import com.my.azusato.exception.ErrorResponse;
import com.my.azusato.integration.AbstractIntegration;
import com.my.azusato.view.controller.common.CookieConstant;
import com.my.azusato.view.controller.common.HttpConstant;
import com.my.azusato.view.controller.common.UrlConstant.Api;

public class UserContollerAPITest extends AbstractIntegration {

	@Autowired
	MockMvc mockMvc;

	@Autowired
	MessageSource ms;

	ObjectMapper om = new ObjectMapper();

	@Nested
	class AddNonMember {

		@Test
		public void normal_case() throws Exception {
			mockMvc.perform(MockMvcRequestBuilders
					.post(TestConstant.MAKE_ABSOLUTE_URL + Api.COMMON_REQUSET + Api.USER_CONTROLLER_REQUSET
							+ Api.ADD_NONMEMBER_URL)
					.content(getRequestBody()).contentType(HttpConstant.DEFAULT_CONTENT_TYPE_STRING)).andDo(print())
					.andExpect(status().isCreated())
					.andExpect(cookie().value(CookieConstant.NON_MEMBER_KEY, notNullValue()));
		}

		@ParameterizedTest
		@MethodSource("com.my.azusato.common.TestSource#locales")
		public void semi_abnormal_case(Locale locale) throws Exception {

			MvcResult mvcResult = mockMvc
					.perform(MockMvcRequestBuilders
							.post(TestConstant.MAKE_ABSOLUTE_URL + Api.COMMON_REQUSET + Api.USER_CONTROLLER_REQUSET
									+ Api.ADD_NONMEMBER_URL)
							.locale(locale).contentType(HttpConstant.DEFAULT_CONTENT_TYPE_STRING)
							.cookie(TestCookie.getNonmemberCookie()))
					.andDo(print()).andExpect(status().isBadRequest()).andReturn();

			String resultBody = mvcResult.getResponse()
					.getContentAsString(Charset.forName(TestConstant.DEFAULT_CHARSET));
			ErrorResponse result = om.readValue(resultBody, ErrorResponse.class);

			assertEquals(new ErrorResponse(AzusatoException.I0003, ms.getMessage(AzusatoException.I0003, null, locale)),
					result);
		}

		private String getRequestBody() throws Exception {
			AddNonMemberUserAPIRequest req = AddNonMemberUserAPIRequest.builder().name(Entity.createdVarChars[0])
					.profileImageType(Entity.createdVarChars[1]).profileImageBase64(Entity.createdVarChars[2]).build();

			return om.writeValueAsString(req);
		}
	}
}
