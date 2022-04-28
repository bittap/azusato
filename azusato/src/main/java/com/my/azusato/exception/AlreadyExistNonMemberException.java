package com.my.azusato.exception;

import java.util.Locale;

import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.thymeleaf.expression.Messages;

public class AlreadyExistNonMemberException extends AzusatoException {

	private static final long serialVersionUID = 1L;
	
	private final static String title = "E-0001";

	public AlreadyExistNonMemberException(Locale locale, MessageSource messageSource) {
		super(HttpStatus.BAD_REQUEST, title, messageSource.getMessage("exception.already-exist-nonmember", null, locale));
	}

}
