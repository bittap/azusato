package com.my.azusato.integration.view.admin;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import com.my.azusato.common.TestConstant;
import com.my.azusato.integration.AbstractIntegration;

public class WeddingControllerTest extends AbstractIntegration {

  @Nested
  public class invitation {

    private static final String URL = "invitation";

    @ParameterizedTest
    @ValueSource(strings = {"/wedding/" + URL, "/" + TestConstant.LOCALE_KO_STR + "/wedding/" + URL,
        "/" + TestConstant.LOCALE_JA_STR + "/wedding/" + URL})
    public void givenUrl_resultNot404(String url) throws Exception {
      mockMvc.perform(MockMvcRequestBuilders.get(url)).andExpect(status().is(200));
    }
  }
}
