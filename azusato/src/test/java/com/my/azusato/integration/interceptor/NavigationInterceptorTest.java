package com.my.azusato.integration.interceptor;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
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
import com.my.azusato.integration.AbstractIntegration;
import com.my.azusato.interceptor.NavigationInterceptor;
import com.my.azusato.login.LoginUser;

public class NavigationInterceptorTest extends AbstractIntegration {

  @Nested
  class postHandle {

    @Nested
    @DisplayName("正常系")
    class normal {
      @Test
      @DisplayName("404ページでモデルがない_結果modelはnull")
      void givenNoModel_whenInvoke404Page_resultModelIsNull() throws Exception {
        MvcResult resultOfMvc =
            mockMvc.perform(MockMvcRequestBuilders.get(TestConstant.MAKE_ABSOLUTE_URL))
                .andDo(print()).andReturn();

        assertNavModelIsNull(resultOfMvc.getModelAndView());
      }

      @Test
      @DisplayName("ログインしていない_結果キーnavModelはnull")
      void givenNoLogin_resultModelNavIsNull() throws Exception {
        MvcResult resultOfMvc =
            mockMvc.perform(MockMvcRequestBuilders.get(TestConstant.MAKE_ABSOLUTE_URL))
                .andDo(print()).andReturn();

        assertNavModelIsNull(resultOfMvc.getModelAndView());
      }

      @Test
      @DisplayName("ログインしていてお祝い通知2件ある_結果キーnavModelの結果2件")
      void givenLoginAndCelebrationNotice2data_resultModelNavIs2data() throws Exception {
        MvcResult resultOfMvc =
            mockMvc.perform(MockMvcRequestBuilders.get(TestConstant.MAKE_ABSOLUTE_URL)
                .with(user(new LoginUser(getAdminUser())))).andDo(print()).andReturn();

        ModelAndView mavOfResult = resultOfMvc.getModelAndView();
        Map<String, Object> mapsOfResult = mavOfResult.getModel();
        Assertions.assertNotNull(mapsOfResult.get(NavigationInterceptor.MODEL_MAP_NAME));

        GetCelebrationNoticesSerivceAPIResponse result =
            (GetCelebrationNoticesSerivceAPIResponse) mapsOfResult
                .get(NavigationInterceptor.MODEL_MAP_NAME);
        Assertions.assertEquals(2, result.getNotices().size());
      }

      private void assertNavModelIsNull(ModelAndView mavOfResult) {
        Map<String, Object> mapsOfResult = mavOfResult.getModel();
        Assertions.assertNull(mapsOfResult.get(NavigationInterceptor.MODEL_MAP_NAME));
      }
    }
  }
}
