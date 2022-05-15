package com.my.azusato.interceptor;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.my.azusato.dto.LoginUserDto;
import com.my.azusato.entity.UserEntity.Type;
import com.my.azusato.property.SessionProperty;
import com.my.azusato.util.SessionUtil;
import com.my.azusato.view.controller.common.CookieConstant;
import com.my.azusato.view.controller.common.SessionConstant;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * セッションに関する。interceptor
 * 
 * @author kim-t
 *
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class SessionInterceptor implements HandlerInterceptor {

	private final SessionProperty sessionProperty;

	/**
	 * セッションがあると維持する。ない場合、非ログイン会員情報があるとそれを基にセッションを登録する。
	 */
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		log.debug("[セッションpreHandle]");
		HttpSession session = request.getSession();
		if(SessionUtil.isLoginSession(request.getSession())) {
			log.debug("[既にログインセッションが存在。log更新]");
			session.setMaxInactiveInterval(sessionProperty.getMaxIntervalSeconds());
			// セッション情報がない かつ 非ログインの場合はログイン扱いにする。
		} else {
			log.debug("[ログインセッションが存在しない。cookie検出]");
			Cookie[] cookies = request.getCookies();
			if (Objects.nonNull(cookies)) {
				Optional<Cookie> opNonmemberCookie = Arrays
						.stream(cookies).peek((e)->log.debug("[cookie] key : {}, value : {}",e.getName(),e.getValue())).filter((c) -> Objects.nonNull(c.getName())
								&& c.getName().equals(CookieConstant.NON_MEMBER_KEY) && Objects.nonNull(c.getValue()))
						.findFirst();

				// 非ログイン会員扱いにする。
				if (opNonmemberCookie.isPresent()) {
					String userNo = opNonmemberCookie.get().getValue();
					log.debug("[非ログイン会員扱いログイン] userNo : {}", userNo);
					LoginUserDto loginDto = LoginUserDto.builder().no(Long.valueOf(userNo))
							.userType(Type.nonmember.toString()).build();
					session.setAttribute(SessionConstant.LOGIN_KEY, loginDto);
					session.setMaxInactiveInterval(sessionProperty.getMaxIntervalSeconds());
					return true;
				}
			}
				
			log.debug("[ログインセッションが存在しません。]");

		}

		return true;
	}
}
