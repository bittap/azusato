package com.my.azusato.config;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.csrf.InvalidCsrfTokenException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.my.azusato.common.AzusatoConstant;
import com.my.azusato.entity.UserEntity;
import com.my.azusato.exception.AzusatoException;
import com.my.azusato.exception.ErrorResponse;
import com.my.azusato.interceptor.LocaleInterceptor;
import com.my.azusato.locale.LocaleConstant;
import com.my.azusato.property.CookieProperty;
import com.my.azusato.view.controller.UserController;
import com.my.azusato.view.controller.common.UrlConstant;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@EnableWebSecurity
@Configuration
@Slf4j
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	
	public static final String API_LOGIN_URL = "/api/login";
	
	public static final String API_LOGOUT_URL = "/api/logout";
	
	public static final String USERNAME_PARAMETER = "id";
	
	public static final String PASSWORD_PARAMETER = "password";
	
	/**
	 * flase : UserNotFoundException, true : BadCredentialsException
	 */
	private final boolean HIDE_USER_NOT_FOUND_EXCEPTIONS = false;
	
	private final UserDetailsService userDetailService;
	
	private final ObjectMapper om = new ObjectMapper();
	
	private final MessageSource ms;
	
	private final CookieProperty cookieProperty;
	
	@Override
	public void configure(WebSecurity web) throws Exception {
		web.ignoring().mvcMatchers(WebMvcConfig.EXCLUDE_PATTERNS);
	}
	
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.authenticationProvider(authProvider());
	}
	
	/**
	 * ログイン、ログアウト、認証に関して設定を行う。
	 * 認証に関して
	 *　<pre>
	 *   /admin, /ko/admin, /ja/adminから始まる場合はadmin権限を要求する。
	 * 　　その以外には誰でも入れる。
	 * </pre>
	 */
	/**
	 *
	 */
	protected void configure(HttpSecurity http) throws Exception {
		List<String> onlyAdminUrls = new ArrayList<>();
		onlyAdminUrls.add(UrlConstant.COMMON_ADMIN_CONTROLLER_REQUSET + "/**");
		for (Locale locale : LocaleConstant.supportedLocales) {
			onlyAdminUrls.add(String.format("/%s%s%s",locale.toString() ,UrlConstant.COMMON_ADMIN_CONTROLLER_REQUSET,"/**"));
		}
		
		log.debug("管理者だけ許可するURL : {}",onlyAdminUrls);
		
		http
			.authorizeRequests()
					.mvcMatchers(onlyAdminUrls.toArray(String[]::new)).hasRole(UserEntity.Type.admin.toString())
					.mvcMatchers("/**").permitAll()
				.and()
			.formLogin()
				.loginProcessingUrl(API_LOGIN_URL)
				.usernameParameter(USERNAME_PARAMETER)
				.passwordParameter(PASSWORD_PARAMETER)
				.successHandler(new AuthenticationSuccessHandler() {
					
					/**
					 * ログインに成功した場合。
					 * 「ユーザID保存」のチェックボックスがcheckedの場合は、「ユーザID保存」に対するクッキーに保持する。
					 * checkedではない場合は、クッキーを削除する。
					 */
					@Override
					public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
							Authentication authentication) throws IOException, ServletException {
						log.debug("ログイン成功");
						log.debug("parametes : {}",request.getParameterMap().keySet()
								.stream()
								.map((e)->
									String.format("%s:%s", e,request.getParameter(e))
								).collect(Collectors.joining(",")));
						String savedId = request.getParameter(cookieProperty.getLoginSaveIdName());
						if(Objects.isNull(savedId)) {
							log.warn("ログインに成功したものの、パラメータに\"{}\"が含まれてないためユーザID保存の追加・削除が行われていなかったです。",cookieProperty.getLoginSaveIdName());
						}else {
							// true
							if(Boolean.valueOf(savedId)) {
								addSaveIdCookie(request.getParameter(USERNAME_PARAMETER), response);
							}else {
								deleteSaveIdCookie(response);
							}
						}
						response.setStatus(HttpStatus.OK.value());
					}
				})
				.failureHandler(new AuthenticationFailureHandler() {
					
					/**
					 *　<ul>
					 *	<li>400 : IDが存在しない、パスワードが一致しない</li>
					 *	<li>500 : その他</li>
					 * </ul>
					 */
					@Override
					public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
							AuthenticationException ex) throws IOException, ServletException {
						// UTF-8
						response.setCharacterEncoding(AzusatoConstant.DEFAULT_CHARSET);
						
						int statusCode;
						ErrorResponse errorResponse = new ErrorResponse();
						if(ex instanceof UsernameNotFoundException) {
							errorResponse.setTitle(AzusatoException.I0009);
							errorResponse.setMessage(ms.getMessage(AzusatoException.I0009, null, request.getLocale()));
							statusCode = HttpStatus.BAD_REQUEST.value();
						}else if(ex instanceof BadCredentialsException) {
							errorResponse.setTitle(AzusatoException.I0010);
							errorResponse.setMessage(ms.getMessage(AzusatoException.I0010, null, request.getLocale()));
							statusCode = HttpStatus.BAD_REQUEST.value();
						}else {
							log.error("ログイン処理で予期せぬエラーが発生しました。",ex);
							
							errorResponse.setTitle(AzusatoException.E0001);
							errorResponse.setMessage(ms.getMessage(AzusatoException.E0001, null, request.getLocale()));
							statusCode = HttpStatus.INTERNAL_SERVER_ERROR.value();
						}
						
						response.getWriter().write(om.writeValueAsString(errorResponse));
						response.setStatus(statusCode);
					}
				})
				.permitAll()
				.and()
			.logout()
				.logoutSuccessHandler(new LogoutSuccessHandler() {
					
					/**
					 * ログアウト成功すると200を返す。
					 */
					@Override
					public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
							throws IOException, ServletException {
						response.setStatus(HttpStatus.OK.value());
					}
				})
				.logoutUrl(API_LOGOUT_URL)
				.and()
			.exceptionHandling()
				// 未認証のユーザーが認証の必要なAPIにアクセスしたとき
				.authenticationEntryPoint(new AuthenticationEntryPoint() {
					
					/**
					 * ログイン画面に遷移させる。
					 */
					@Override
					public void commence(HttpServletRequest request, HttpServletResponse response,
							AuthenticationException ex) throws IOException, ServletException {
						log.warn("未認証のユーザーが認証の必要なAPIにアクセス",ex);
						response.sendRedirect(getLoginUrl(request));
					}
				})
				// ユーザーは認証済みだが未認可のリソースへアクセスしたときの処理
				.accessDeniedHandler(new AccessDeniedHandler() {
					
					/**
					 * CSRF Tokenエラーの場合はエラーレスポンスを返す。画面の場合はログイン画面に遷移させる。
					 */
					@Override
					public void handle(HttpServletRequest request, HttpServletResponse response,
							AccessDeniedException ex) throws IOException, ServletException {
						log.warn("ユーザーは認証済みだが未認可のリソースへアクセス",ex);
						
						int statusCode;
						ErrorResponse errorResponse = new ErrorResponse();
						
						if(ex instanceof InvalidCsrfTokenException) {
							errorResponse.setTitle(AzusatoException.W0002);
							errorResponse.setMessage(ms.getMessage(AzusatoException.W0002, null, request.getLocale()));
							statusCode = HttpStatus.FORBIDDEN.value();
							// UTF-8
							response.setCharacterEncoding(AzusatoConstant.DEFAULT_CHARSET);
							response.getWriter().write(om.writeValueAsString(errorResponse));
							response.setStatus(statusCode);
						}else {
							response.sendRedirect(getLoginUrl(request));
						}
						


						
					}
				});
	};
	
	/**
	 * クッキーを追加する。
	 * {@link SecurityConfig#addOrdeleteSaveIdCookie(String, HttpServletResponse)}に処理を委員する。
	 */
	private void addSaveIdCookie(String id , HttpServletResponse response) {
		addOrdeleteSaveIdCookie(id, response);
	}
	
	/**
	 * クッキーを削除する。
	 * {@link SecurityConfig#addOrdeleteSaveIdCookie(String, HttpServletResponse)}に処理を委員する。
	 */
	private void deleteSaveIdCookie(HttpServletResponse response) {
		addOrdeleteSaveIdCookie(null, response);
	}
	
	
	/**
	 * "id"がtrueの場合はクッキーにIDを保持する。その以外はクッキーを削除する。
	 * @param isSave notEmpty : 追加、empty : 削除
	 * @param response クッキーを保持・削除する。
	 */
	private void addOrdeleteSaveIdCookie(String id , HttpServletResponse response) {
		int maxAge;
		if(StringUtils.isEmpty(id)) {
			maxAge = 0;
		}else {
			maxAge = cookieProperty.getCookieMaxTime();
		}
		Cookie cookie = new Cookie(cookieProperty.getLoginSaveIdName(), id);
		cookie.setMaxAge(maxAge);
		cookie.setPath(cookieProperty.getPath());
		response.addCookie(cookie);
	}
	
	/**
	 * ロケールによるログイン画面URL取得
	 * @param request
	 * @return ログイン画面URL
	 */
	private String getLoginUrl(HttpServletRequest request) {
		return LocaleInterceptor.getLocale(request) == Locale.KOREAN ? 
				UrlConstant.KOREAN_CONTROLLER_REQUEST + UrlConstant.USER_CONTROLLER_REQUSET + UserController.USER_LOGIN_URL 
				: UrlConstant.JAPANESE_CONTROLLER_REQUEST + UrlConstant.USER_CONTROLLER_REQUSET + UserController.USER_LOGIN_URL;
	}
	
	/**
	 * 基本のbcrypt使用する。
	 * @return
	 */
	@Bean
	public PasswordEncoder passwordEncoder() {
	    return PasswordEncoderFactories.createDelegatingPasswordEncoder();
	}
	
	/**
	 * setHideUserNotFoundExceptionsのために、独自に生成する。
	 * @return 
	 */
	private AuthenticationProvider authProvider() {
		DaoAuthenticationProvider impl = new DaoAuthenticationProvider();
		impl.setHideUserNotFoundExceptions(HIDE_USER_NOT_FOUND_EXCEPTIONS);
		impl.setPasswordEncoder(passwordEncoder());
		impl.setUserDetailsService(userDetailService);
		
		return impl;
	}
}
