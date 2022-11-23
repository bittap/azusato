package com.my.azusato.common;

import java.util.Locale;
import java.util.stream.Stream;
import org.junit.jupiter.params.provider.Arguments;
import com.my.azusato.common.TestMessage.I0002;
import com.my.azusato.common.TestMessage.I0003;

public class TestSource {

  public static Stream<Arguments> locales() {
    return Stream.of(Arguments.of(Locale.JAPANESE), Arguments.of(Locale.KOREAN));
  }

  public static Stream<Arguments> notAdminLogin() {
    return Stream.of(Arguments.of(TestLogin.lineLoginUser()),
        Arguments.of(TestLogin.kakaoLoginUser()), Arguments.of(TestLogin.nonmemberLoginUser()));
  }

  public static Stream<Arguments> givenNoLogin_result401() {
    return Stream.of(Arguments.of(TestConstant.LOCALE_JA, "ログインが必要です。ログインしてください。"),
        Arguments.of(TestConstant.LOCALE_KO, "로그인이 필요합니다.로그인 해주세요."));
  }

  public static Stream<Arguments> I0003_Message() {
    return Stream.of( //
        Arguments.of(Locale.JAPANESE, I0003.JAPANESE), //
        Arguments.of(Locale.KOREAN, I0003.KOREAN) //
    );
  }


  public static Stream<Arguments> I0002_Message() {
    return Stream.of( //
        Arguments.of(Locale.JAPANESE, I0002.JAPANESE), //
        Arguments.of(Locale.KOREAN, I0002.KOREAN) //
    );
  }

  public static Stream<Arguments> I0001_Message() {
    return Stream.of( //
        Arguments.of(Locale.JAPANESE, "ログインが必要です。ログインしてください。"), //
        Arguments.of(Locale.KOREAN, "로그인이 필요합니다.로그인 해주세요.") //
    );
  }

  public static Stream<Arguments> I0005_user_Message() {
    return Stream.of( //
        Arguments.of(Locale.JAPANESE, "ユーザ情報が存在しないです。"), //
        Arguments.of(Locale.KOREAN, "유저정보가 존재하지 않습니다.") //
    );
  }

  public static Stream<Arguments> I0005_celebration_Message() {
    return Stream.of( //
        Arguments.of(Locale.JAPANESE, "お祝い情報が存在しないです。"), //
        Arguments.of(Locale.KOREAN, "축하정보가 존재하지 않습니다.")); //
  }

  public static Stream<Arguments> I0006_Message() {
    return Stream.of( //
        Arguments.of(Locale.JAPANESE, "権限が存在しません。"), //
        Arguments.of(Locale.KOREAN, "권한이없습니다.") //
    );
  }

  public static Stream<Arguments> E0002_Message() {
    return Stream.of( //
        Arguments.of(Locale.JAPANESE, "指定したパスにファイルが存在しないため、内容を読み込むのに失敗しました。"), //
        Arguments.of(Locale.KOREAN, "지정된 경로에 파일이 존재하지 않아 내용을 읽는데 실패했습니다.") //
    );
  }

  public static Stream<Arguments> I0008_Message() {
    return Stream.of( //
        Arguments.of(Locale.JAPANESE, "不正な値が存在します。"), //
        Arguments.of(Locale.KOREAN, "올바르지 않은 정보가 존재합니다.") //
    );
  }
}
