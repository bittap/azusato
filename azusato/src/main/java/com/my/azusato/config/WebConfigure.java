package com.my.azusato.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.my.azusato.interceptor.LocaleInterCeptor;


//@EnableWebMvc // use WebMvcConfigurer
@Configuration
public class WebConfigure implements WebMvcConfigurer {
	
	/**
	 * for adding interceptors
	 */
	private final LocaleInterCeptor LOCALE_INTERCEPTOR;
	
	/**
	 * 
	 * @param LocaleInterCeptor for adding interceptors
	 */
	@Autowired
	public WebConfigure(LocaleInterCeptor LocaleInterCeptor) {
		this.LOCALE_INTERCEPTOR = LocaleInterCeptor;
	}
	
//	@Override
//	public void addResourceHandlers(ResourceHandlerRegistry registry) {
//		 registry.addResourceHandler("/static/**");
//	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		// register LocaleInterCeptor
		registry.addInterceptor(LOCALE_INTERCEPTOR)
				.addPathPatterns("/**")
				//exclude static resocues
				.excludePathPatterns("/css/**", "/js/**", "/images/**");
	}
}
