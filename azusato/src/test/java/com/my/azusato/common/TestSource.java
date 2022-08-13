package com.my.azusato.common;

import java.util.Locale;
import java.util.stream.Stream;

import org.junit.jupiter.params.provider.Arguments;

public class TestSource {

	public static Stream<Arguments> locales() {
		return Stream.of(Arguments.of(Locale.JAPANESE), Arguments.of(Locale.KOREAN));
	}
	
	public static Stream<Arguments> notAdminLogin() {
		return Stream.of(
				Arguments.of(TestLogin.lineLoginUser()),
				Arguments.of(TestLogin.kakaoLoginUser()),
				Arguments.of(TestLogin.nonmemberLoginUser())
			);
	}
	
	public static Stream<Arguments> givenNoLogin_result401(){
		return Stream.of(
				Arguments.of(TestConstant.LOCALE_JA,"ログインが必要です。ログインしてください。"),
				Arguments.of(TestConstant.LOCALE_KO,"로그인이 필요합니다.로그인 해주세요.")
		);
	}
}
