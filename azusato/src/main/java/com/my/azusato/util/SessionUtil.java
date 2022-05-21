package com.my.azusato.util;

import java.util.Objects;
import java.util.Optional;

import javax.servlet.http.HttpSession;

import com.my.azusato.dto.LoginUserDto;
import com.my.azusato.view.controller.common.SessionConstant;

/**
 * セッションに関する楽なメソッド
 * @author Carmel
 *
 */
public class SessionUtil {

	/**
	 * ログインセッション情報があるか確認
	 * @param httpSession {@link HttpSession}
	 * @return true : ログインセッション情報がある , false : ない。
	 */
	public static boolean isLoginSession(HttpSession httpSession) {
		if(Objects.isNull(httpSession.getAttribute(SessionConstant.LOGIN_KEY))) {
			return false;
		}else {
			return true;
		}
	}
	
	/**
	 * loginSession情報を返す。
	 * @param httpSession {@link HttpSession}
	 * @return loginSession情報。もし、ない場合はnullを返す。
	 */
	public static Optional<LoginUserDto> getLoginSession(HttpSession httpSession) {
		
		if(isLoginSession(httpSession)) {
			LoginUserDto userDto = (LoginUserDto)httpSession.getAttribute(SessionConstant.LOGIN_KEY);
			return Optional.ofNullable(userDto);
		} else {
			return Optional.ofNullable(null);
		}
	}
}
