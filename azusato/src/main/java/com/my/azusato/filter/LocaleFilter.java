package com.my.azusato.filter;

//import java.io.IOException;
//import java.util.Locale;
//
//import javax.servlet.Filter;
//import javax.servlet.FilterChain;
//import javax.servlet.ServletException;
//import javax.servlet.ServletRequest;
//import javax.servlet.ServletResponse;
//import javax.servlet.http.HttpServletRequest;
//
//import org.springframework.core.annotation.Order;
//import org.springframework.stereotype.Component;
//import org.springframework.web.servlet.LocaleResolver;
//
//import lombok.RequiredArgsConstructor;
//
//@Component
//@RequiredArgsConstructor
//@Order(1) // 優先順位高い
//public class LocaleFilter implements Filter {
//	
//	private final LocaleResolver localeResolver;
//	
//	public static final String LOCALE_ATTRIBUTE_NAME = LocaleFilter.class.getName() + ".LOCALE";
//
//	@Override
//	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
//			throws IOException, ServletException {
//		HttpServletRequest req = (HttpServletRequest) request;
//		
//		request.setAttribute(LOCALE_ATTRIBUTE_NAME, localeResolver.resolveLocale(req));
//	}
//	
//	/**
//	 * 解決されたロケールを取得する。
//	 * requestにある属性{@link LocaleInterceptor#LOCALE_ATTRIBUTE_NAME}を取得する。
//	 * @param request ロケール取得のためのrequest
//	 * @return prehandle処理より解決されたロケール
//	 */
//	public static Locale getLocale(HttpServletRequest request) {
//		return  (Locale)request.getAttribute(LOCALE_ATTRIBUTE_NAME);
//	}
//
//}
