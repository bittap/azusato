package com.my.azusato.exception;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.TreeSet;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.TypeMismatchException;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@ControllerAdvice
@Slf4j
@RequiredArgsConstructor
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

  private final HttpHeaders headers = new HttpHeaders();

  private final MessageSource ms;

  private final HttpServletRequest httpServletRequest;

  @ExceptionHandler(value = {Exception.class})
  @ApiResponses(value = {@ApiResponse(responseCode = "500", description = "予期せぬエラー",
      content = {@Content(mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponse.class))}),})
  public ResponseEntity<Object> handleMyException(Exception ex) {
    log.error("error", ex);

    if (ex instanceof AzusatoException) {
      AzusatoException azusatoException = (AzusatoException) ex;
      ErrorResponse responseBody =
          new ErrorResponse(azusatoException.getTitle(), azusatoException.getMessage());
      return new ResponseEntity<>(responseBody, headers, azusatoException.getStatus());
    } else if (ex instanceof ResponseStatusException) {
      ResponseStatusException responseStatusException = (ResponseStatusException) ex;
      ErrorResponse responseBody =
          new ErrorResponse(responseStatusException.getStatus().getReasonPhrase(),
              responseStatusException.getReason());
      return new ResponseEntity<>(responseBody, headers,
          responseStatusException.getRawStatusCode());
    } else {
      ErrorResponse responseBody = new ErrorResponse(AzusatoException.E0001,
          ms.getMessage(AzusatoException.E0001, null, httpServletRequest.getLocale()));
      return new ResponseEntity<>(responseBody, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  /**
   * 容量を超えた場合のエラーのハンドリング
   * 
   * @param ex 容量超えエラー
   * @param request エラーメッセージ用
   * @return {@link AzusatoException#I0011}エラー
   */
  @ExceptionHandler(value = {MaxUploadSizeExceededException.class})
  public ResponseEntity<ErrorResponse> handleFileSizeException(MaxUploadSizeExceededException ex,
      WebRequest request) {
    log.info("アップロードサイズを超えたエラー", ex.getMessage());

    ErrorResponse responseBody = new ErrorResponse(AzusatoException.I0011,
        ms.getMessage(AzusatoException.I0011, null, request.getLocale()));
    return new ResponseEntity<>(responseBody, HttpStatus.BAD_REQUEST);
  }

  @Override
  protected ResponseEntity<Object> handleTypeMismatch(TypeMismatchException ex, HttpHeaders headers,
      HttpStatus status, WebRequest request) {
    ErrorResponse responseBody = new ErrorResponse(AzusatoException.I0008,
        ms.getMessage(AzusatoException.I0008, null, request.getLocale()));

    return new ResponseEntity<>(responseBody, headers, status);
  }


  /**
   * {@code ModelAttribute}でエラーが起きた場合と{@code ResponseBody}とタイプミスまっうちが起きた場合。エラーメッセージを作って400エラーコードを返す。
   * 該当するフィールド名が存在しないと、プログラムのフィールド名を使う。 タイプエラーの場合は、コードの3番目を使う。
   * もし、ValidationMessages.propertiesから取得したメッセージに{0}マッピングに失敗するとステータス500を返却する。 メッセージは\nで追加される。
   */
  @Override
  protected ResponseEntity<Object> handleBindException(BindException ex, HttpHeaders headers,
      HttpStatus status, WebRequest request) {
    // タイプエラーの場合
    // codes例、3番目を使う。
    // typeMismatch.myPageControllerRequest.currentPageNo
    // typeMismatch.currentPageNo
    // typeMismatch.java.lang.Integer
    // typeMismatch
    final int CODE_INDEX = 2;
    Locale locale = request.getLocale();

    TreeSet<String> errorMsgs = new TreeSet<>();

    for (FieldError fieldError : ex.getFieldErrors()) {
      if (fieldError.shouldRenderDefaultMessage()) {
        String fieldName;
        try {
          fieldName = ms.getMessage(fieldError.getField(), null, locale);
        } catch (NoSuchMessageException e) {
          log.warn("not any bounded field name from message source. code : {}",
              fieldError.getField());
          fieldName = fieldError.getField();
        }

        try {
          String errorMsg;
          // タイプエラー
          if (fieldError.isBindingFailure() == true) {
            errorMsg =
                ms.getMessage(fieldError.getCodes()[CODE_INDEX], new String[] {fieldName}, locale);
          } else {
            errorMsg = MessageFormat.format(fieldError.getDefaultMessage(), fieldName);
          }

          errorMsgs.add(errorMsg);
        } catch (IllegalArgumentException | NullPointerException e) {
          log.error("error", ex);
          log.error("fail to bounded message. default message : {}, fieldName : {}",
              fieldError.getDefaultMessage(), fieldName);
          return this.handleMyException(e);
        }
      } else {
        errorMsgs.add(fieldError.getDefaultMessage());
      }

    }

    ErrorResponse responseBody = new ErrorResponse(AzusatoException.I0004,
        errorMsgs.stream().collect(Collectors.joining("\n")));

    return new ResponseEntity<>(responseBody, headers, status);
  }


  /**
   * {@code @RequestBody}でエラーが起きた場合 {@link #handleBindException}に委任する。
   */
  @Override
  protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
      HttpHeaders headers, HttpStatus status, WebRequest request) {

    return handleBindException(ex, headers, status, request);
  }
}
