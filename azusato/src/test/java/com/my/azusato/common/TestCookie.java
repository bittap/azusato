package com.my.azusato.common;

import javax.servlet.http.Cookie;

import com.my.azusato.view.controller.common.CookieConstant;

public class TestCookie {

	public static Cookie getNonmemberCookie() {
		return new Cookie(CookieConstant.NON_MEMBER_KEY, "1");
	}
}
