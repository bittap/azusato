package com.my.azusato.exception;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.my.azusato.view.controller.common.HttpConstant;

import lombok.extern.slf4j.Slf4j;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

	ObjectMapper om = new ObjectMapper();

	private final HttpHeaders headers;

	public GlobalExceptionHandler() {
		headers = new HttpHeaders();
		headers.setContentType(HttpConstant.DEFAULT_CONTENT_TYPE);
	}

	@ExceptionHandler(value = { Exception.class })
	public ResponseEntity<Object> handleMyException(Exception ex) throws JsonProcessingException {
		log.error("error : {}", ex.getStackTrace());

		if (ex instanceof AzusatoException) {
			AzusatoException azusatoException = (AzusatoException) ex;
			ErrorResponse responseBody = new ErrorResponse(azusatoException.getTitle(), azusatoException.getMessage());
			return new ResponseEntity<>(om.writeValueAsString(responseBody), headers, azusatoException.getStatus());
		} else {
			return new ResponseEntity<>("", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
