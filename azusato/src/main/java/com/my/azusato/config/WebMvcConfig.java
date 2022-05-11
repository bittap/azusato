package com.my.azusato.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.my.azusato.interceptor.LogInterceptor;
import com.my.azusato.interceptor.SessionInterceptor;

/**
 * web関連configクラス。
 * 
 * @author kim-t
 *
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

	@Autowired
	SessionInterceptor sessionInterceptor;
	
	@Autowired
	LogInterceptor logInterceptor;
	
	private final static String[] EXCLUDE_PATTERNS = {"/css/**", "/external/**", "/favicon/**", "/js/**", "/image/**", "/music/**", "/video/**","/favicon.ico"};

	/**
	 * interceptorを登録する。
	 */
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(sessionInterceptor).addPathPatterns("/**").excludePathPatterns(EXCLUDE_PATTERNS);
		registry.addInterceptor(logInterceptor).addPathPatterns("/**").excludePathPatterns(EXCLUDE_PATTERNS);
	}
}
