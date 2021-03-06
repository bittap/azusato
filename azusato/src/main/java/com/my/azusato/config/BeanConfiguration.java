package com.my.azusato.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.LocaleResolver;

import com.my.azusato.locale.LocalePathAndHeaderResolver;
import com.my.azusato.property.RestProperty;

import lombok.RequiredArgsConstructor;

/**
 * beanを宣言するクラス。
 * 
 * @author kim-t
 *
 */
@Configuration
@RequiredArgsConstructor
public class BeanConfiguration {

	private final RestProperty restProperty;
	
	@Bean
	public RestTemplate restTemplate() {
		RestTemplate rt = new RestTemplate();
		return rt;
	}
	
	@Bean
	public LocaleResolver localeResolver() {
		return new LocalePathAndHeaderResolver();
	}
}
