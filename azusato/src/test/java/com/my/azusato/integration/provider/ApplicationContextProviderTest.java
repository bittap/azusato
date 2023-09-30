package com.my.azusato.integration.provider;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import com.my.azusato.integration.AbstractIntegration;
import com.my.azusato.provider.ApplicationContextProvider;

public class ApplicationContextProviderTest extends AbstractIntegration {

  @Nested
  class getApplicationContext {

    @Test
    void returnConext() {
      Assertions.assertNotNull(ApplicationContextProvider.getApplicationContext());
    }
  }

  @Nested
  class getCurrentHttpRequest {
    @Test
    void returnCurrentHttpRequest() {
      Assertions.assertNotNull(ApplicationContextProvider.getCurrentHttpRequest());
    }
  }
}
