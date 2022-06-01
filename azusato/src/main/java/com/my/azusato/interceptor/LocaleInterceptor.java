package com.my.azusato.interceptor;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.my.azusato.locale.LocalePathAndHeaderResolver;

import lombok.RequiredArgsConstructor;

/**
 * ログに関する。interceptor
 * 
 * @author kim-t
 *
 */
@Component
@RequiredArgsConstructor
public class LocaleInterceptor implements HandlerInterceptor {
	
	/**
	 * prehandleにより、解決したlocale
	 */
	public static Locale resovledLocaleWhenPreHandle;
	
	private final LocalePathAndHeaderResolver localeResolver;
	
	/**
	 *　 Localeを解決する。
	 */
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		
		resovledLocaleWhenPreHandle = localeResolver.resolveLocale(request);
		return true;
	}
}
