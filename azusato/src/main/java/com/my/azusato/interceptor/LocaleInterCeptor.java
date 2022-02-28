package com.my.azusato.interceptor;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.support.RequestContextUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * Interceptor for locale. resolve language by locale for using thymeleaf
 * @author Carmel
 *
 */
@Slf4j
@Component
public class LocaleInterCeptor implements HandlerInterceptor {
	
	/**
	 * resolve language by locale for using thymeleaf
	 */
	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		
		log.debug("RequestContext's default locale : {}",RequestContextUtils.getLocale(request));
		log.debug("postHandle's default locale : {}",Locale.getDefault());
		log.debug("postHandle's request locale : {}",request.getLocale());
		log.debug("postHandle's request locale : {}",request.getLocale());
		log.debug("postHandle's locale : {}",response.getLocale());
		response.getLocale();
	}
	
	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		log.debug("afterCompletion's locale : {}",response.getLocale());
		response.getLocale();
	}
}
