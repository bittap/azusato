package com.my.azusato.locale;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleResolver;

import lombok.extern.slf4j.Slf4j;

/**
 * configure for beans
 * @author TB_KimTaeYeong
 *
 */
@Configuration
@Slf4j
public class LocaleBean {

	@Bean
	public LocaleResolver localeResolver() {
		log.info("localResolver create bean"); 
		return new LocalePathResolver();
	}
}
