package com.my.azusato.integration.view;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import java.nio.charset.Charset;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Stream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.servlet.ModelAndView;
import com.my.azusato.common.TestConstant;
import com.my.azusato.common.TestLogin;
import com.my.azusato.exception.AzusatoException;
import com.my.azusato.exception.ErrorResponse;
import com.my.azusato.integration.AbstractIntegration;
import com.my.azusato.view.controller.CelebrationController;
import com.my.azusato.view.controller.common.ModelConstant;
import com.my.azusato.view.controller.common.UrlConstant;
import com.my.azusato.view.controller.response.CelebrationModifyResponse;
import com.my.azusato.view.controller.response.HeaderReponse;

public class CelebrationContrllerTest extends AbstractIntegration {

  @Nested
  public class list {

    @ParameterizedTest
    @ValueSource(strings = {UrlConstant.CELEBRATION_CONTROLLER_REQUSET,
        UrlConstant.JAPANESE_CONTROLLER_REQUEST + UrlConstant.CELEBRATION_CONTROLLER_REQUSET,
        UrlConstant.KOREAN_CONTROLLER_REQUEST + UrlConstant.CELEBRATION_CONTROLLER_REQUSET})
    public void givenUrl_resultNot404(String url) throws Exception {
      mockMvc.perform(MockMvcRequestBuilders.get(url)).andExpect(status().is(200));
    }
  }

  @Nested
  public class redirectListFromNotice {

    @ParameterizedTest
    @ValueSource(
        strings = {UrlConstant.CELEBRATION_CONTROLLER_REQUSET + "/redirect/list/from-notice/1",
            UrlConstant.JAPANESE_CONTROLLER_REQUEST + UrlConstant.CELEBRATION_CONTROLLER_REQUSET
                + "/redirect/list/from-notice/1",
            UrlConstant.KOREAN_CONTROLLER_REQUEST + UrlConstant.CELEBRATION_CONTROLLER_REQUSET
                + "/redirect/list/from-notice/1"})
    public void givenUrl_resultNot404(String url) throws Exception {
      mockMvc.perform(MockMvcRequestBuilders.get(url).with(user(TestLogin.adminLoginUser())))
          .andExpect(status().is(302));
    }
  }

  @Nested
  public class Write {

    @ParameterizedTest
    @ValueSource(strings = {
        UrlConstant.CELEBRATION_CONTROLLER_REQUSET + CelebrationController.CELEBRATION_WRITE_URL,
        UrlConstant.JAPANESE_CONTROLLER_REQUEST + UrlConstant.CELEBRATION_CONTROLLER_REQUSET
            + CelebrationController.CELEBRATION_WRITE_URL,
        UrlConstant.KOREAN_CONTROLLER_REQUEST + UrlConstant.CELEBRATION_CONTROLLER_REQUSET
            + CelebrationController.CELEBRATION_WRITE_URL})
    public void givenUrl_resultNot404(String url) throws Exception {
      mockMvc.perform(MockMvcRequestBuilders.get(url)).andExpect(status().is(200));
    }

    /**
     * @throws Exception
     */
    @Test
    public void givenNoSession_resultOk() throws Exception {
      MvcResult mvcResult = mockMvc
          .perform(MockMvcRequestBuilders.get(UrlConstant.CELEBRATION_CONTROLLER_REQUSET
              + CelebrationController.CELEBRATION_WRITE_URL))
          .andDo(print()).andExpect(status().isOk()).andReturn();

      ModelAndView mavResult = mvcResult.getModelAndView();

      Map<String, Object> mapsOfResult = mavResult.getModel();

      compareHeader(mapsOfResult);
    }

  }

  @Nested
  public class Modify {

    public final String celebrationNo = "1";

    public final String celebrationNoPath = "/" + celebrationNo;

    @ParameterizedTest
    @ValueSource(strings = {
        UrlConstant.CELEBRATION_CONTROLLER_REQUSET + CelebrationController.CELEBRATION_WRITE_URL
            + celebrationNoPath,
        UrlConstant.JAPANESE_CONTROLLER_REQUEST + UrlConstant.CELEBRATION_CONTROLLER_REQUSET
            + CelebrationController.CELEBRATION_WRITE_URL + celebrationNoPath,
        UrlConstant.KOREAN_CONTROLLER_REQUEST + UrlConstant.CELEBRATION_CONTROLLER_REQUSET
            + CelebrationController.CELEBRATION_WRITE_URL + celebrationNoPath})
    public void givenUrl_resultNot404(String url) throws Exception {
      mockMvc.perform(MockMvcRequestBuilders.get(url)).andExpect(status().is(200));
    }

    /**
     * @throws Exception
     */
    @Test
    public void givenNoSession_resultOk() throws Exception {
      MvcResult mvcResult = mockMvc
          .perform(MockMvcRequestBuilders.get(UrlConstant.CELEBRATION_CONTROLLER_REQUSET
              + CelebrationController.CELEBRATION_WRITE_URL + celebrationNoPath))
          .andDo(print()).andExpect(status().isOk()).andReturn();

      ModelAndView mavResult = mvcResult.getModelAndView();

      Map<String, Object> mapsOfResult = mavResult.getModel();

      CelebrationModifyResponse response =
          (CelebrationModifyResponse) mapsOfResult.get(ModelConstant.DATA_KEY);
      CelebrationModifyResponse expect =
          CelebrationModifyResponse.builder().celebrationNo(Long.valueOf(celebrationNo)).build();

      Assertions.assertEquals(expect, response);


      compareHeader(mapsOfResult);
    }

    @ParameterizedTest
    @MethodSource("com.my.azusato.integration.view.CelebrationContrllerTest#modify_givenBadRequest_result400")
    public void givenBadRequest_result400(Locale locale, String url, String expectedMessage)
        throws Exception {
      MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get(url).locale(locale))
          .andDo(print()).andExpect(status().is(400)).andReturn();

      String resultBody =
          mvcResult.getResponse().getContentAsString(Charset.forName(TestConstant.DEFAULT_CHARSET));
      ErrorResponse result = om.readValue(resultBody, ErrorResponse.class);

      assertEquals(new ErrorResponse(AzusatoException.I0008, expectedMessage), result);
    }

  }

  @SuppressWarnings("unused")
  private static Stream<Arguments> modify_givenBadRequest_result400() {

    return Stream.of(
        Arguments.of(TestConstant.LOCALE_JA,
            UrlConstant.CELEBRATION_CONTROLLER_REQUSET + CelebrationController.CELEBRATION_WRITE_URL
                + "/string",
            "不正な値が存在します。"),
        Arguments.of(TestConstant.LOCALE_KO, UrlConstant.CELEBRATION_CONTROLLER_REQUSET
            + CelebrationController.CELEBRATION_WRITE_URL + "/string", "올바르지 않은 정보가 존재합니다."));
  }

  private void compareHeader(Map<String, Object> mapsOfResult) {
    HeaderReponse resultHr = (HeaderReponse) mapsOfResult.get(ModelConstant.HEADER_KEY);
    HeaderReponse expectHr = new HeaderReponse();
    expectHr.setCelebration(true);

    Assertions.assertEquals(expectHr, resultHr);
  }
}
