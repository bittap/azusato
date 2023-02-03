package com.my.azusato.filter;

import static org.mockito.Mockito.when;
import java.security.Principal;
import javax.servlet.FilterChain;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import org.jboss.logging.MDC;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
public class MDCFilterTest {

  MDCFilter target = new MDCFilter();

  @Mock
  HttpServletRequest req;

  @Mock
  Principal principal;

  @Nested
  class doFilter {

    @Mock
    ServletResponse response;

    @Mock
    FilterChain chain;

    @DisplayName("正常系")
    @Nested
    class normal {

      @Test
      void givenLogInUser_resultDoesNotThrow() throws Exception {
        Assertions.assertDoesNotThrow(() -> {
          String userId = "logInUserId";

          mockGetUserId(userId);

          target.doFilter(req, response, chain);
        });
      }
    }
  }

  @Nested
  class getUserId {

    String PRIVATE_METHOD_NAME = "getUserId";

    @DisplayName("正常系")
    @Nested
    class normal {

      @Test
      void givenLogInUser_resultReturnUserId() {
        String userId = "logInUserId";

        mockGetUserId(userId);

        String result = ReflectionTestUtils.invokeMethod(target, PRIVATE_METHOD_NAME, req);
        Assertions.assertEquals(userId, result);
      }

      @Test
      void givenNoLogInUser_resultReturnNull() {
        when(req.getUserPrincipal()).thenReturn(null);

        String result = ReflectionTestUtils.invokeMethod(target, PRIVATE_METHOD_NAME, req);
        Assertions.assertNull(result);
      }
    }
  }

  @Nested
  class registerUserId {

    @DisplayName("正常系")
    @Nested
    class normal {

      String PRIVATE_METHOD_NAME = "registerUserId";

      @Test
      void givenUserIdIsNull_resultNotRegistedMDC() {
        String userId = null;
        ReflectionTestUtils.invokeMethod(target, PRIVATE_METHOD_NAME, userId);
        Assertions.assertNull(MDC.get(target.USER_ID_KEY));
      }

      @Test
      void givenUserIdIsNotNull_resultRegistedMDC() {
        String userId = "userId";
        ReflectionTestUtils.invokeMethod(target, PRIVATE_METHOD_NAME, userId);
        Assertions.assertEquals(userId, MDC.get(target.USER_ID_KEY));
      }

      @AfterEach
      void clearMDC() {
        MDC.clear();
      }
    }
  }

  private void mockGetUserId(String userId) {
    when(req.getUserPrincipal()).thenReturn(principal);
    when(principal.getName()).thenReturn(userId);
  }
}
