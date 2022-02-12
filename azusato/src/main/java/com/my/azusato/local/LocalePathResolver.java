package com.my.azusato.local;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.LocaleResolver;

import lombok.extern.slf4j.Slf4j;

/**
 * Resolve local PathValue
 * @author Carmel
 *
 */
@Slf4j
public class LocalePathResolver implements LocaleResolver {

	@Override
	public Locale resolveLocale(HttpServletRequest request) {
		log.info("!!resolveLocale!!");
		String getPath = request.getRequestURI();
		log.info("getPath : {}",getPath);
		
//		String language = getPath.split("/")[1];
//		
//		if(language.equals("ko")) {
//			log.info("this language is korea");
//			return Locale.KOREAN;
//		}else {
//			return Locale.JAPANESE;
//		}
		
		return Locale.JAPANESE;
	}

	@SuppressWarnings("static-access")
	@Override
	public void setLocale(HttpServletRequest request, HttpServletResponse response, Locale locale) {
		throw new UnsupportedOperationException("Method of setLocale is not supported");
	}

	
}
