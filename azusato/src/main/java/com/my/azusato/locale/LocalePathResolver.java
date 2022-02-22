package com.my.azusato.locale;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.util.Strings;
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
		String getPath = request.getRequestURI();
		log.info("getPath : {}",getPath);

		// make locale by path
		if(Strings.isNotEmpty(getPath)) {
			String[] paths = getPath.split("/");
			if(paths.length > 1) {
				String language = paths[1];
				if(language.equals("ko")) {
					log.info("containt specific languae : Korean");
					return Locale.KOREAN;
				}else if(language.equals("jp")){
					log.info("containt specific languae : JAPANESE");
					return Locale.JAPANESE;
				}
			}
		}

		// default
		log.info("not containt specific languae :"+request.getLocale());
		return request.getLocale();

	}

	@Override
	public void setLocale(HttpServletRequest request, HttpServletResponse response, Locale locale) {
		throw new UnsupportedOperationException("Method of setLocale is not supported");
	}

	
}
