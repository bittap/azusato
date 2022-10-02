package com.my.azusato.unit.api.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import java.nio.charset.Charset;
import java.util.Locale;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.my.azusato.anonotation.UnitController;
import com.my.azusato.api.controller.CelebrationNoticeControllerAPI;
import com.my.azusato.api.service.CelebrationNoticeServiceAPI;
import com.my.azusato.common.TestConstant;
import com.my.azusato.exception.AzusatoException;
import com.my.azusato.exception.ErrorResponse;
import com.my.azusato.view.controller.common.UrlConstant.Api;

@UnitController
@Import(value = CelebrationNoticeControllerAPI.class)
class CelebrationNoticeControllerApiTest {

  @Autowired
  MockMvc mockMvc;

  ObjectMapper om = new ObjectMapper();

  @MockBean
  CelebrationNoticeServiceAPI mockCeleNoticeService;

  @Nested
  class celebrationNotices {

    @Nested
    @DisplayName("正常系")
    @Disabled("結合テストで対応")
    class normal {

    }

    @Nested
    @DisplayName("準正常系")
    class subnormal {

      @ParameterizedTest
      @MethodSource("com.my.azusato.common.TestSource#I0001_Message")
      @DisplayName("セッションがない_結果401をスローする")
      public void givenNoSession_resultThrow401(Locale locale, String expectMessage)
          throws Exception {
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders
            .get(TestConstant.MAKE_ABSOLUTE_URL + Api.COMMON_REQUSET
                + CelebrationNoticeControllerAPI.CELEBRATION_NOTICES_URL)
            .with(csrf()).locale(locale)).andDo(print()).andExpect(status().is(401)).andReturn();

        String resultBody = mvcResult.getResponse()
            .getContentAsString(Charset.forName(TestConstant.DEFAULT_CHARSET));
        ErrorResponse result = om.readValue(resultBody, ErrorResponse.class);

        assertEquals(new ErrorResponse(AzusatoException.I0001, expectMessage), result);
      }
    }
  }

  @Nested
  class read {

    final String CELEBRATION_REPLY_NO = "1";

    final String CELEBRATION_NO = "1";

    final String READCOUNTUP_URL =
        CelebrationNoticeControllerAPI.COMMON_URL + "/read/" + CELEBRATION_NO;

    @Nested
    @DisplayName("正常系")
    @Disabled("結合テストで対応")
    class normal {

    }

    @Nested
    @DisplayName("準正常系")
    class subnormal {

      @ParameterizedTest
      @MethodSource("com.my.azusato.common.TestSource#I0001_Message")
      @DisplayName("セッションがない_結果401をスローする")
      public void givenNoSession_result401(Locale locale, String expectMessage) throws Exception {
        MvcResult mvcResult = mockMvc
            .perform(MockMvcRequestBuilders
                .put(TestConstant.MAKE_ABSOLUTE_URL + Api.COMMON_REQUSET + READCOUNTUP_URL)
                .with(csrf()).locale(locale))
            .andDo(print()).andExpect(status().is(401)).andReturn();

        String resultBody = mvcResult.getResponse()
            .getContentAsString(Charset.forName(TestConstant.DEFAULT_CHARSET));
        ErrorResponse result = om.readValue(resultBody, ErrorResponse.class);

        assertEquals(new ErrorResponse(AzusatoException.I0001, expectMessage), result);
      }
    }
  }
}
