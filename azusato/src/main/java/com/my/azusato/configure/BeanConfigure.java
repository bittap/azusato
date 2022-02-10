package com.my.azusato.configure;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import lombok.extern.slf4j.Slf4j;


/**
 * configure for beans
 * @author TB_KimTaeYeong
 *
 */
@Configuration
@Slf4j
public class BeanConfigure {

	@Bean
	public LocaleResolver localeResolver() {
		log.info("localResolver create bean");
		SessionLocaleResolver localeResolver = new SessionLocaleResolver();
		return localeResolver;
	}
	
	@Bean
	public LocaleChangeInterceptor localChangeInterceptor() {
		log.info("LocaleChangeInterceptor create bean");
		LocaleChangeInterceptor lci = new LocaleChangeInterceptor();
		//lci.setParamName("lang");
		return lci;
	}
}
