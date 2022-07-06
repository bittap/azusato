package com.my.azusato.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.my.azusato.interceptor.LocaleInterceptor;
import com.my.azusato.interceptor.LogInterceptor;
import com.my.azusato.interceptor.LoginInterceptor;
import com.my.azusato.interceptor.NavigationInterceptor;

import lombok.RequiredArgsConstructor;

/**
 * web関連configクラス。
 * 
 * @author kim-t
 *
 */
@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

	private final LoginInterceptor sessionInterceptor;
	
	private final LogInterceptor logInterceptor;
	
	private final LocaleInterceptor localeInterceptor;
	
	private final NavigationInterceptor navInterceptor;
	
	public final static String[] EXCLUDE_PATTERNS = {"/css/**", "/manifest/**","/external/**", "/favicon/**", "/js/**", "/image/**", "/music/**", "/video/**","/favicon.ico"};

	/**
	 * interceptorを登録する。
	 */
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(sessionInterceptor).addPathPatterns("/**").excludePathPatterns(EXCLUDE_PATTERNS);
		registry.addInterceptor(logInterceptor).addPathPatterns("/**").excludePathPatterns(EXCLUDE_PATTERNS);
		registry.addInterceptor(localeInterceptor).addPathPatterns("/**").excludePathPatterns(EXCLUDE_PATTERNS);
		registry.addInterceptor(navInterceptor).addPathPatterns("/**").excludePathPatterns(EXCLUDE_PATTERNS);
	}
}
