package com.my.azusato.locale;

import java.util.Iterator;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;

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
public class LocalePathAndHeaderResolver implements LocaleResolver {
	
	@Override
	public Locale resolveLocale(HttpServletRequest request) {
		final String PATH = request.getRequestURI();
		// make locale by path
		Locale localeBypath = resolveByPath(PATH);
		
		// if Locale resolved by path use that
		if(Objects.nonNull(localeBypath)) {
			return localeBypath;
		}
		
		// resolve locale by header 
		Locale localeByHeader = resolveByHeader(request.getLocales().asIterator());
		
		// if Locale resolved by header use that
		if(Objects.nonNull(localeByHeader)) {
			return localeByHeader;
		}
		
		// default
		log.info("Because neither path or header, resolve default's locales which is japanese");
		
		return LocaleConstant.DEFAULT_LOCALE;
	}
	
	/**
	 * resolve locale by header of client.
	 * @param clientLocales clinet's request locales
	 * @return ko : Locale.KOREAN, en : Locale.JAPANESE , others : null 
	 */
	private Locale resolveByHeader(Iterator<Locale> clientLocales) {
		while (clientLocales.hasNext()) {
			Locale clientLocale = clientLocales.next();
			for (Locale supportLocale : LocaleConstant.supportedLocales) {
				if(clientLocale == supportLocale) {
					return supportLocale;
				}
			}
		}
		return null;
	}

	/**
	 * resolve locale by path of next host. 
	 * <pre>
	 * localhost:8080/ko/... =>Locale.KOREAN
	 * localhost:8080/en/... =>Locale.JAPANESE
	 * </pre>
	 * @param getPath clinet's request url
	 * @return ko : Locale.KOREAN, en : Locale.JAPANESE , others : null 
	 */
	private Locale resolveByPath(String getPath) {
		log.debug("resolveByPath, getPath : {}",getPath);
		if(Strings.isNotEmpty(getPath)) {
			String[] paths = getPath.split("/");
			// exist path next host
			if(paths.length > 1) {
				String languageOfPath = paths[1];
				Set<String> keys = LocaleConstant.supportedLocaleMap.keySet();
				for (String key : keys) {
					if(languageOfPath.equals(key)) {
						log.info("contain specific languae : {}",LocaleConstant.supportedLocaleMap.get(key));
						return LocaleConstant.supportedLocaleMap.get(key);
					}
				}
			}
		}
		return null;
	}

	/**
	 * Not use
	 */
	@Override
	public void setLocale(HttpServletRequest request, HttpServletResponse response, Locale locale) {
		throw new UnsupportedOperationException("Method of setLocale is not supported");
	}

	
}
