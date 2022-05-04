package com.my.azusato.exception;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.my.azusato.view.controller.common.HttpConstant;
import com.my.azusato.view.controller.common.MessageConstant;

import lombok.extern.slf4j.Slf4j;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

	private final HttpHeaders headers;

	private final MessageSource ms;

	public GlobalExceptionHandler(MessageSource ms) {
		headers = new HttpHeaders();
		headers.setContentType(HttpConstant.DEFAULT_CONTENT_TYPE);
		this.ms = ms;
	}

	@ExceptionHandler(value = { Exception.class })
	public ResponseEntity<Object> handleMyException(Exception ex) throws JsonProcessingException {
		log.error("error : {}", ex.getStackTrace());

		if (ex instanceof AzusatoException) {
			AzusatoException azusatoException = (AzusatoException) ex;
			ErrorResponse responseBody = new ErrorResponse(azusatoException.getTitle(), azusatoException.getMessage());
			return new ResponseEntity<>(responseBody, headers, azusatoException.getStatus());
		} else if (ex instanceof ResponseStatusException) {
			ResponseStatusException responseStatusException = (ResponseStatusException) ex;
			// exclude message of error code
			String message = responseStatusException.getMessage().split("\"")[1];
			ErrorResponse responseBody = new ErrorResponse(responseStatusException.getStatus().getReasonPhrase(),
					message);
			return new ResponseEntity<>(responseBody, headers, responseStatusException.getRawStatusCode());
		} else {
			return new ResponseEntity<>("", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * {@code @RequestBody}でエラーが起きた場合、エラーメッセージを作って400エラーコードを返す。
	 * 該当するフィールド名が存在しないと、プログラムのフィールド名を使う。
	 * もし、ValidationMessages.propertiesから取得したメッセージに{0}マッピングに失敗するとステータス500を返却する。
	 * メッセージは\nで追加される。
	 */
	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {
		Locale locale = request.getLocale();

		TreeSet<String> errorMsgs = new TreeSet<>();

		for (FieldError fieldError : ex.getFieldErrors()) {
			if (fieldError.shouldRenderDefaultMessage()) {
				String fieldName;
				try {
					fieldName = ms.getMessage(fieldError.getField(), null, locale);
				} catch (NoSuchMessageException e) {
					log.warn("not any bounded field name from message source. code : {}", fieldError.getField());
					fieldName = fieldError.getField();
				}

				try {
					String errorMsg = MessageFormat.format(fieldError.getDefaultMessage(), fieldName);

					errorMsgs.add(errorMsg);
				} catch (IllegalArgumentException e) {
					log.error("fail to bounded message. default message : {}, fieldName : {}",
							fieldError.getDefaultMessage(), fieldName);
					ErrorResponse responseBody = new ErrorResponse(status.getReasonPhrase(),
							ms.getMessage(MessageConstant.ERROR500, null, locale));
					return new ResponseEntity<>(responseBody, headers, HttpStatus.INTERNAL_SERVER_ERROR);
				}
			} else {
				errorMsgs.add(fieldError.getDefaultMessage());
			}

		}

		ErrorResponse responseBody = new ErrorResponse(status.getReasonPhrase(),
				errorMsgs.stream().collect(Collectors.joining("\n")));

		return new ResponseEntity<>(responseBody, headers, status);
	}
}
