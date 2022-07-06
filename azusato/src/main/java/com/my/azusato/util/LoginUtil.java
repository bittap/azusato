package com.my.azusato.util;

import java.util.Objects;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.my.azusato.login.LoginUser;

public class LoginUtil {

	
	/**
	 * ログインしたユーザ情報を返す。
	 * @return ログインしたユーザ情報
	 * @throws NullPointerException ログインしたユーザ情報が存在しない。
	 */
	public static LoginUser getLoginUser() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if(Objects.isNull(auth))
			throw new NullPointerException("ログインしたユーザ情報が存在しません。");
		
		return (LoginUser)auth.getPrincipal();
	}
}
