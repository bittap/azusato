package com.my.azusato.configure;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;

import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class WebConfigure implements WebMvcConfigurer {
	
	@Autowired
	private LocaleChangeInterceptor lci;
	
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		log.info("addInterceptors");
	    registry.addInterceptor(lci);
	}

}
