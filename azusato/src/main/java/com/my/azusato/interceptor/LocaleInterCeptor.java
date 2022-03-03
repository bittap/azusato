package com.my.azusato.interceptor;

import java.util.Objects;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.support.RequestContextUtils;

import com.my.azusato.annotation.NeedLocaleInterceptor;
import com.my.azusato.exception.ForgetHeaderModel;
import com.my.azusato.locale.LocalePathAndHeaderResolver;
import com.my.azusato.view.controller.common.ModelConstant;
import com.my.azusato.view.controller.response.HeaderReponse;

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
	 * resolve language resovled by {@link LocalePathAndHeaderResolver#resolveLocale(HttpServletRequest)} for using thymeleaf.
	 * @throws ForgetHeaderModel If handler have {@link NeedLocaleInterceptor} annotation, doesn't have model {@link HeaderReponse}
	 * @throws ClassCastException type of header model is not {@link HeaderReponse} 
	 */
	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		if(handler instanceof HandlerMethod) {
			NeedLocaleInterceptor localeInterceptorByAnno = ((HandlerMethod) handler).getMethodAnnotation(NeedLocaleInterceptor.class);
			// exist annotation of NeedLocaleInterceptor
			if(Objects.nonNull(localeInterceptorByAnno)) {
				// get header model
				Object headerObject = modelAndView.getModel().get(ModelConstant.HEADER_KEY);
				
				if(Objects.isNull(headerObject)) {
					throw new ForgetHeaderModel();
				}else {
					if(headerObject instanceof HeaderReponse) {
						log.debug("RequestContext's default locale : {}",RequestContextUtils.getLocale(request).getDisplayLanguage());
						((HeaderReponse) headerObject).setLanguage(RequestContextUtils.getLocale(request).getLanguage());
					}else {
						throw new ClassCastException(String.format("type of %s is not %s", ModelConstant.HEADER_KEY, HeaderReponse.class.getSimpleName()));
					}
				}
			}
		}
	}
}
