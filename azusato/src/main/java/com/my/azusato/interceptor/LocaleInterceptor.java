package com.my.azusato.interceptor;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.LocaleResolver;

import lombok.RequiredArgsConstructor;

/**
 * ロケールを解決する。
 * 
 * @author kim-t
 *
 */
@Component
@RequiredArgsConstructor
public class LocaleInterceptor implements HandlerInterceptor {
	
	private final LocaleResolver localeResolver;
	
	public static final String LOCALE_ATTRIBUTE_NAME = LocaleInterceptor.class.getName() + ".LOCALE";
	
	/**
	 *　 Localeを解決する。
	 */
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		
		request.setAttribute(LOCALE_ATTRIBUTE_NAME, localeResolver.resolveLocale(request));
		return true;
	}
	
	/**
	 * 解決されたロケールを取得する。
	 * requestにある属性{@link LocaleInterceptor#LOCALE_ATTRIBUTE_NAME}を取得する。
	 * @param request ロケール取得のためのrequest
	 * @return prehandle処理より解決されたロケール
	 */
	public static Locale getLocale(HttpServletRequest request) {
		return  (Locale)request.getAttribute(LOCALE_ATTRIBUTE_NAME);
	}
}
