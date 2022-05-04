package com.my.azusato.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * beanを宣言するクラス。
 * 
 * @author kim-t
 *
 */
@Configuration
public class BeanConfiguration {

	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}
}
