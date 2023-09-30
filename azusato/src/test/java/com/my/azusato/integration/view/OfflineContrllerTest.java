package com.my.azusato.integration.view;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import com.my.azusato.integration.AbstractIntegration;

public class OfflineContrllerTest extends AbstractIntegration {

  @Nested
  public class initalize {

    @ParameterizedTest
    @ValueSource(strings = {"/offline"})
    public void givenUrl_resultNot404(String url) throws Exception {
      mockMvc.perform(MockMvcRequestBuilders.get(url)).andExpect(status().is(200));
    }
  }
}
