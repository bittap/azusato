package com.my.azusato.unit.api.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import java.nio.charset.Charset;
import java.util.Locale;
import java.util.stream.Stream;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.my.azusato.anonotation.UnitController;
import com.my.azusato.api.controller.WeddingAttenderControllerAPI;
import com.my.azusato.api.controller.request.CreateWeddingAttendRequest;
import com.my.azusato.api.service.WeddingAttenderServiceAPI;
import com.my.azusato.common.TestConstant;
import com.my.azusato.entity.WeddingAttender.Nationality;
import com.my.azusato.exception.AzusatoException;
import com.my.azusato.exception.ErrorResponse;
import com.my.azusato.view.controller.common.HttpConstant;
import com.my.azusato.view.controller.common.UrlConstant.Api;

@UnitController
@Import(value = WeddingAttenderControllerAPI.class)
class WeddingAttenderControllerApiTest {

  @Autowired
  MockMvc mockMvc;

  ObjectMapper om = new ObjectMapper();

  @MockBean
  WeddingAttenderServiceAPI service;

  @Nested
  class create {

    @Nested
    @DisplayName("正常系")
    class normal {

      @Test
      void ok() throws Exception {
        mockMvc
            .perform(MockMvcRequestBuilders
                .post(
                    TestConstant.MAKE_ABSOLUTE_URL + Api.COMMON_REQUSET + "/wedding/" + "attender")
                .with(csrf()) //
                .content(om.writeValueAsString(vaildParameter()))
                .contentType(HttpConstant.DEFAULT_CONTENT_TYPE_STRING)
                .accept(HttpConstant.DEFAULT_CONTENT_TYPE))
            .andExpect(status().isCreated()).andReturn();
      }
    }

    @Nested
    @DisplayName("準正常系")
    class subnormal {

      @ParameterizedTest
      @MethodSource("com.my.azusato.unit.api.controller.WeddingAttenderControllerApiTest#create_subnormal_givenInVaildParameter_resultBadRequest")
      void givenInVaildParameter_resultBadRequest(Locale locale,
          CreateWeddingAttendRequest reqestBody, String expectedMessage) throws Exception {
        MvcResult mvcResult =
            mockMvc
                .perform(MockMvcRequestBuilders
                    .post(TestConstant.MAKE_ABSOLUTE_URL + Api.COMMON_REQUSET + "/wedding/"
                        + "attender")
                    .with(csrf()) //
                    .locale(locale).content(om.writeValueAsString(reqestBody))
                    .contentType(HttpConstant.DEFAULT_CONTENT_TYPE_STRING)
                    .accept(HttpConstant.DEFAULT_CONTENT_TYPE))
                .andDo(print()).andExpect(status().is(400)).andReturn();

        String resultBody = mvcResult.getResponse()
            .getContentAsString(Charset.forName(TestConstant.DEFAULT_CHARSET));
        ErrorResponse result = om.readValue(resultBody, ErrorResponse.class);

        assertEquals(new ErrorResponse(AzusatoException.I0004, expectedMessage), result);
      }
    }

    CreateWeddingAttendRequest vaildParameter() {
      String name = "name";
      String nationality = Nationality.KOREA.toString();
      boolean attend = true;
      boolean eatting = true;
      String remark = "remark";

      return new CreateWeddingAttendRequest(name, nationality, attend, eatting, remark);
    }
  }

  static Stream<Arguments> create_subnormal_givenInVaildParameter_resultBadRequest()
      throws Exception {
    create testClass = new WeddingAttenderControllerApiTest().new create();

    CreateWeddingAttendRequest para1 = testClass.vaildParameter();
    para1.setName(null);

    CreateWeddingAttendRequest para2 = testClass.vaildParameter();
    para2.setName(RandomStringUtils.randomAlphabetic(11));

    CreateWeddingAttendRequest para3 = testClass.vaildParameter();
    para3.setNationality(null);

    CreateWeddingAttendRequest para4 = testClass.vaildParameter();
    para4.setAttend(null);

    CreateWeddingAttendRequest para5 = testClass.vaildParameter();
    para5.setEatting(null);

    CreateWeddingAttendRequest para6 = testClass.vaildParameter();
    para6.setRemark(RandomStringUtils.randomAlphabetic(1001));

    CreateWeddingAttendRequest para7 = testClass.vaildParameter();
    para7.setNationality("invalid");


    return Stream.of(Arguments.of(TestConstant.LOCALE_JA, para1, "名前は必修項目です。"),
        Arguments.of(TestConstant.LOCALE_JA, para2, "名前は最大10桁数まで入力可能です。"),
        Arguments.of(TestConstant.LOCALE_JA, para3, "国籍は必修項目です。"),
        Arguments.of(TestConstant.LOCALE_JA, para4, "参加は必修項目です。"),
        Arguments.of(TestConstant.LOCALE_JA, para5, "食事は必修項目です。"),
        Arguments.of(TestConstant.LOCALE_JA, para6, "備考は最大1000桁数まで入力可能です。"),
        Arguments.of(TestConstant.LOCALE_JA, para7, "国籍は不正な値です。"),

        Arguments.of(TestConstant.LOCALE_KO, para1, "이름을 입력해주세요."),
        Arguments.of(TestConstant.LOCALE_KO, para2, "글자 수 10을 초과해서 이름을 입력하는 것은 불가능합니다."),
        Arguments.of(TestConstant.LOCALE_KO, para3, "국적을 입력해주세요."),
        Arguments.of(TestConstant.LOCALE_KO, para4, "참가을 입력해주세요."),
        Arguments.of(TestConstant.LOCALE_KO, para5, "식사을 입력해주세요."),
        Arguments.of(TestConstant.LOCALE_KO, para6, "글자 수 1000을 초과해서 비고을 입력하는 것은 불가능합니다."),
        Arguments.of(TestConstant.LOCALE_KO, para7, "국적을 올바르게 입력해주세요."));
  }
}
