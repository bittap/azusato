package com.my.azusato.exception;

import java.util.Locale;

import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;

public class AlreadyExistNonMemberException extends AzusatoException {

	private static final long serialVersionUID = 1L;

	public AlreadyExistNonMemberException(Locale locale, MessageSource messageSource) {
		super(HttpStatus.BAD_REQUEST, HttpStatus.BAD_REQUEST.getReasonPhrase(),
				messageSource.getMessage("exception.already-exist-nonmember", null, locale));
	}

}
