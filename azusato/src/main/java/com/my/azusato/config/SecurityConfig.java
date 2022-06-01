package com.my.azusato.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.my.azusato.entity.UserEntity;
import com.my.azusato.locale.LocaleConstant;
import com.my.azusato.view.controller.common.UrlConstant;

import lombok.extern.slf4j.Slf4j;

@EnableWebSecurity
@Configuration
@Slf4j
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	
	public static final String API_LOGIN_URL = "/api/login";
	
	public static final String API_LOGOUT_URL = "/api/logout";
	
	public static final String USERNAME_PARAMETER = "id";
	
	public static final String PASSWORD_PARAMETER = "password";
	
	/**
	 * ログイン、ログアウト、認証に関して設定を行う。
	 * 認証に関して
	 *　<pre>
	 *   /admin, /ko/admin, /ja/adminから始まる場合はadmin権限を要求する。
	 * 　　その以外には誰でも入れる。
	 * </pre>
	 */
	protected void configure(org.springframework.security.config.annotation.web.builders.HttpSecurity http) throws Exception {
		List<String> onlyAdminUrls = new ArrayList<>();
		onlyAdminUrls.add(UrlConstant.COMMON_ADMIN_CONTROLLER_REQUSET + "/**");
		for (Locale locale : LocaleConstant.supportedLocales) {
			onlyAdminUrls.add(String.format("/%s%s%s",locale.toString() ,UrlConstant.COMMON_ADMIN_CONTROLLER_REQUSET,"/**"));
		}
		
		log.debug("管理者だけ許可するURL : {}",onlyAdminUrls);
		
		http
			.authorizeRequests()
					.mvcMatchers("/**").permitAll()
					.mvcMatchers(onlyAdminUrls.toArray(String[]::new)).hasRole(UserEntity.Type.admin.toString())
				.and()
			.formLogin()
				.loginProcessingUrl(API_LOGIN_URL)
				.usernameParameter(USERNAME_PARAMETER)
				.passwordParameter(PASSWORD_PARAMETER)
				.permitAll()
				.and()
			.logout()
				.logoutUrl(API_LOGOUT_URL);
	};
	
	@Bean
	PasswordEncoder passwordEncoder() {
	    return new BCryptPasswordEncoder();
	}
}
