package com.my.azusato.view.controller.response;

import com.my.azusato.interceptor.LocaleInterCeptor;
import com.my.azusato.locale.LocalePathAndHeaderResolver;

import lombok.Data;

@Data
public class HeaderReponse {

	/**
	 * language resolved by {@link LocalePathAndHeaderResolver}. this field is made on {@link LocaleInterCeptor}
	 */
	private String language;
}
