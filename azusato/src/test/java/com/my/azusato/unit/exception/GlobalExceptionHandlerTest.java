package com.my.azusato.unit.exception;

import static org.mockito.Mockito.when;
import java.util.Locale;
import java.util.stream.Stream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.server.ResponseStatusException;
import com.my.azusato.exception.AzusatoException;
import com.my.azusato.exception.ErrorResponse;
import com.my.azusato.exception.GlobalExceptionHandler;
import com.my.azusato.integration.AbstractIntegration;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GlobalExceptionHandlerTest extends AbstractIntegration {

  @Autowired
  private GlobalExceptionHandler errorHandler;

  @Value("${spring.servlet.multipart.max-file-size}")
  private String maxFileSize;

  @Mock
  WebRequest request;

  @Nested
  class handleMyException {

    @Test
    void givenResponseStatusException_resultReturnResponseStatusException() {
      String reason = "エラーメッセージ";
      HttpStatus status = HttpStatus.BAD_REQUEST;
      ResponseStatusException ex = new ResponseStatusException(status, reason);

      ResponseEntity<Object> result = errorHandler.handleMyException(ex);

      ErrorResponse resultBody = (ErrorResponse) result.getBody();
      HttpStatus resultStatus = result.getStatusCode();
      Assertions.assertEquals(status.getReasonPhrase(), resultBody.getTitle());
      Assertions.assertEquals(reason, resultBody.getMessage());
      Assertions.assertEquals(status, resultStatus);
    }

    @Test
    void givenUnExpectedException_resultServerError() {
      String reason = "予期せぬエラーが発生しました。";
      HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
      Exception ex = new Exception(reason);

      ResponseEntity<Object> result = errorHandler.handleMyException(ex);

      ErrorResponse resultBody = (ErrorResponse) result.getBody();
      HttpStatus resultStatus = result.getStatusCode();
      Assertions.assertEquals("E-0001", resultBody.getTitle());
      Assertions.assertEquals(reason, resultBody.getMessage());
      Assertions.assertEquals(status, resultStatus);
    }
  }

  /*
   * SpringBootTestではテストができないため、tomcatエラー
   */
  @Nested
  class HandleFileSizeException {


    @ParameterizedTest
    @MethodSource("com.my.azusato.unit.exception.GlobalExceptionHandlerTest#HandleFileSizeException_givenError_result400")
    public void givenError_result400(Locale locale, String message) throws Exception {
      when(request.getLocale()).thenReturn(locale);

      log.debug("maxFileSize: {}", maxFileSize);
      MaxUploadSizeExceededException ex = new MaxUploadSizeExceededException(10 * (1024 * 1024));

      ResponseEntity<ErrorResponse> errorResult = errorHandler.handleFileSizeException(ex, request);
      log.debug("result: {}", errorResult);

      ErrorResponse expect = new ErrorResponse(AzusatoException.I0011, message);

      Assertions.assertEquals(HttpStatus.BAD_REQUEST, errorResult.getStatusCode());
      Assertions.assertEquals(expect, errorResult.getBody());
    }
  }

  public static Stream<Arguments> HandleFileSizeException_givenError_result400() {
    return Stream.of(Arguments.of(Locale.JAPANESE, "アップロード可能な容量を超えました。"),
        Arguments.of(Locale.KOREAN, "업로드 가능한 용량을 초과했습니다."));
  }


}
