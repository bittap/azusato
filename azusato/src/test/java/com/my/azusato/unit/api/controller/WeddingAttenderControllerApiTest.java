package com.my.azusato.unit.api.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.my.azusato.anonotation.UnitControllerForTest;
import com.my.azusato.api.controller.WeddingAttenderControllerAPI;
import com.my.azusato.api.controller.request.CreateWeddingAttendRequest;
import com.my.azusato.api.service.WeddingAttenderServiceAPI;
import com.my.azusato.api.service.response.GetWeddingAttenderServiceAPIResponse;
import com.my.azusato.common.TestConstant;
import com.my.azusato.entity.WeddingAttender;
import com.my.azusato.entity.WeddingAttender.Nationality;
import com.my.azusato.exception.AzusatoException;
import com.my.azusato.exception.ErrorResponse;
import com.my.azusato.unit.api.request.CreateWeddingAttendRequestTest;
import com.my.azusato.view.controller.common.HttpConstant;
import com.my.azusato.view.controller.common.UrlConstant.Api;

@UnitControllerForTest
@Import(value = WeddingAttenderControllerAPI.class)
class WeddingAttenderControllerApiTest {

  @Autowired
  MockMvc mockMvc;

  ObjectMapper om = JsonMapper.builder().addModule(new JavaTimeModule()).build();

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
      return CreateWeddingAttendRequestTest.getValid();
    }
  }

  @Nested
  class get {

    @Nested
    @DisplayName("正常系")
    class normal {

      @Test
      void ok() throws Exception {
        GetWeddingAttenderServiceAPIResponse response =
            new GetWeddingAttenderServiceAPIResponse(List.of(validWeddingAttender()), 1, 16);

        when(service.get(Mockito.any())).thenReturn(response);

        MvcResult mvcResult =
            mockMvc
                .perform(MockMvcRequestBuilders
                    .get(TestConstant.MAKE_ABSOLUTE_URL + Api.COMMON_REQUSET + "/wedding/"
                        + "attenders")
                    .contentType(HttpConstant.DEFAULT_CONTENT_TYPE_STRING).params(validParams())
                    .accept(HttpConstant.DEFAULT_CONTENT_TYPE))
                .andDo(print()).andExpect(status().isOk()).andReturn();

        String resultBody = mvcResult.getResponse()
            .getContentAsString(Charset.forName(TestConstant.DEFAULT_CHARSET));
        GetWeddingAttenderServiceAPIResponse result =
            om.readValue(resultBody, GetWeddingAttenderServiceAPIResponse.class);

        assertEquals(response, result);
      }

      WeddingAttender validWeddingAttender() {
        String name = "name";
        Nationality nationality = Nationality.KOREA;
        boolean attend = true;
        boolean eatting = true;
        String remark = "remark";
        byte attenderNumber = 10;

        return WeddingAttender.builder() //
            .name(name) //
            .nationality(nationality) //
            .attend(attend) //
            .eatting(eatting) //
            .remark(remark) //
            .attenderNumber(attenderNumber) //
            .build();
      }
    }

    @Nested
    @DisplayName("準正常系")
    class subnormal {

      @ParameterizedTest
      @MethodSource("com.my.azusato.unit.api.controller.WeddingAttenderControllerApiTest#get_subnormal_givenInVaildParameter_resultBadRequest")
      void givenInVaildParameter_resultBadRequest(Locale locale,
          MultiValueMap<String, String> params, String expectedMessage) throws Exception {
        MvcResult mvcResult =
            mockMvc
                .perform(MockMvcRequestBuilders
                    .get(TestConstant.MAKE_ABSOLUTE_URL + Api.COMMON_REQUSET + "/wedding/"
                        + "attenders")
                    .locale(locale).contentType(HttpConstant.DEFAULT_CONTENT_TYPE_STRING)
                    .params(params).accept(HttpConstant.DEFAULT_CONTENT_TYPE))
                .andDo(print()).andExpect(status().isBadRequest()).andReturn();

        String resultBody = mvcResult.getResponse()
            .getContentAsString(Charset.forName(TestConstant.DEFAULT_CHARSET));
        ErrorResponse result = om.readValue(resultBody, ErrorResponse.class);

        assertEquals(new ErrorResponse(AzusatoException.I0004, expectedMessage), result);
      }
    }

    MultiValueMap<String, String> validParams() {
      MultiValueMap<String, String> params = new LinkedMultiValueMap<>();

      params.set("offset", String.valueOf(0));
      params.set("limit", String.valueOf(10));

      return params;
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

    CreateWeddingAttendRequest para8 = testClass.vaildParameter();
    para8.setAttenderNumber((byte) -1);

    return Stream.of(Arguments.of(TestConstant.LOCALE_JA, para1, "名前は必修項目です。"),
        Arguments.of(TestConstant.LOCALE_JA, para2, "名前は最大10桁数まで入力可能です。"),
        Arguments.of(TestConstant.LOCALE_JA, para3, "国籍は必修項目です。"),
        Arguments.of(TestConstant.LOCALE_JA, para4, "参加は必修項目です。"),
        Arguments.of(TestConstant.LOCALE_JA, para5, "食事は必修項目です。"),
        Arguments.of(TestConstant.LOCALE_JA, para6, "備考は最大1000桁数まで入力可能です。"),
        Arguments.of(TestConstant.LOCALE_JA, para7, "国籍は不正な値です。"),
        Arguments.of(TestConstant.LOCALE_JA, para8, "最小0以上の参加者数を入力してください。"),

        Arguments.of(TestConstant.LOCALE_KO, para1, "이름을 입력해주세요."),
        Arguments.of(TestConstant.LOCALE_KO, para2, "글자 수 10을 초과해서 이름을 입력하는 것은 불가능합니다."),
        Arguments.of(TestConstant.LOCALE_KO, para3, "국적을 입력해주세요."),
        Arguments.of(TestConstant.LOCALE_KO, para4, "참가을 입력해주세요."),
        Arguments.of(TestConstant.LOCALE_KO, para5, "식사을 입력해주세요."),
        Arguments.of(TestConstant.LOCALE_KO, para6, "글자 수 1000을 초과해서 비고을 입력하는 것은 불가능합니다."),
        Arguments.of(TestConstant.LOCALE_KO, para7, "국적을 올바르게 입력해주세요."),
        Arguments.of(TestConstant.LOCALE_KO, para8, "최소0이상의 참석인원을 입력해주세요."));
  }

  static Stream<Arguments> get_subnormal_givenInVaildParameter_resultBadRequest() throws Exception {
    get testClass = new WeddingAttenderControllerApiTest().new get();

    var para1 = testClass.validParams();
    para1.set("nationality", "invalid");

    var para2 = testClass.validParams();
    para2.set("division", "invalid");

    var para3 = testClass.validParams();
    para3.set("limit", String.valueOf(0));

    return Stream.of(Arguments.of(TestConstant.LOCALE_JA, para1, "国籍は不正な値です。"),
        Arguments.of(TestConstant.LOCALE_JA, para2, "区分は不正な値です。"),
        Arguments.of(TestConstant.LOCALE_JA, para3, "最小1以上の参照する数を入力してください。"),

        Arguments.of(TestConstant.LOCALE_KO, para1, "국적을 올바르게 입력해주세요."),
        Arguments.of(TestConstant.LOCALE_KO, para2, "구분을 올바르게 입력해주세요."),
        Arguments.of(TestConstant.LOCALE_KO, para3, "최소1이상의 조회수을 입력해주세요."));
  }
}
