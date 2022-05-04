package com.my.azusato.common;

import org.springframework.mock.web.MockHttpSession;

import com.my.azusato.dto.LoginUserDto;
import com.my.azusato.entity.UserEntity.Type;
import com.my.azusato.view.controller.common.SessionConstant;

public class TestSession {

	public static MockHttpSession getAdminSession() {
		MockHttpSession session = new MockHttpSession();

		LoginUserDto dto = new LoginUserDto();
		dto.setNo(1L);
		dto.setUserType(Type.admin.toString());
		session.setAttribute(SessionConstant.LOGIN_KEY, dto);

		return session;
	}

	public static MockHttpSession getKakaoSession() {
		MockHttpSession session = new MockHttpSession();

		LoginUserDto dto = new LoginUserDto();
		dto.setNo(1L);
		dto.setUserType(Type.kakao.toString());
		session.setAttribute(SessionConstant.LOGIN_KEY, dto);

		return session;
	}
}
