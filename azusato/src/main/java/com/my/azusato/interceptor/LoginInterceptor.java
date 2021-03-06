package com.my.azusato.interceptor;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.my.azusato.entity.UserEntity;
import com.my.azusato.entity.UserEntity.Type;
import com.my.azusato.login.Grant;
import com.my.azusato.login.LoginUser;
import com.my.azusato.property.SessionProperty;
import com.my.azusato.repository.UserRepository;
import com.my.azusato.view.controller.common.CookieConstant;

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
public class LoginInterceptor implements HandlerInterceptor {

	private final SessionProperty sessionProperty;
	
	private final UserRepository userRepo;

	/**
	 * ログイン情報があると維持する。ない場合、非ログイン会員情報があるとそれを基にログイン情報を登録する。
	 */
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		log.debug("[セッションpreHandle]");
		HttpSession session = request.getSession();
		if(Objects.nonNull(request.getUserPrincipal())) {
			log.debug("[既にログイン情報が存在。セッション更新]");
			session.setMaxInactiveInterval(sessionProperty.getMaxIntervalSeconds());
			// ログイン情報がない かつ 非ログインの場合はログイン扱いにする。
		} else {
			log.debug("[ログイン情報が存在しない。cookie検出]");
			Cookie[] cookies = request.getCookies();
			if (Objects.nonNull(cookies)) {
				Optional<Cookie> opNonmemberCookie = Arrays
						.stream(cookies).peek((e)->log.debug("[cookie] key : {}, value : {}",e.getName(),e.getValue())).filter((c) -> Objects.nonNull(c.getName())
								&& c.getName().equals(CookieConstant.NON_MEMBER_KEY) && Objects.nonNull(c.getValue()))
						.findFirst();

				// 非ログイン会員扱いにする。
				if (opNonmemberCookie.isPresent()) {
					String userName = opNonmemberCookie.get().getValue();
					UserEntity userEntityByCookie = userRepo.findByIdAndUserTypeAndCommonFlagDeleteFlag(userName, Type.nonmember.toString() , false ).orElseThrow(()->{
						throw new NullPointerException(String.format("保持したCookieのIDにより参照したユーザテーブル情報が存在しません。 userName : %s , type : %s",userName , Type.nonmember.toString()));
					});
					log.debug("[非ログイン会員扱いログイン] userName : {}", userName);
					Authentication authentication = new UsernamePasswordAuthenticationToken(new LoginUser(userEntityByCookie), null,
							AuthorityUtils.createAuthorityList(Grant.NONMEMBER_ROLE));
					SecurityContextHolder.getContext().setAuthentication(authentication);
					
					return true;
				}
			}
				
			log.debug("[ログイン情報が存在しません。]");

		}

		return true;
	}
}
