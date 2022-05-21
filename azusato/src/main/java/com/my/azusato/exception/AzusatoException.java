package com.my.azusato.exception;

import java.util.Locale;

import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class AzusatoException extends RuntimeException {

	/**
	 * ログインが必要だけど、ログインしていない場合
	 */
	public static final String I0001 = "I-0001";

	/**
	 * セッションにログイン情報がない場合
	 */
	public static final String I0002 = "I-0002";

	/**
	 * 既に存在する非会員
	 */
	public static final String I0003 = "I-0003";
	
	/**
	 * パラメータエラー
	 */
	public static final String I0004 = "I-0004";

	/**
	 * テーブル情報が存在しない場合
	 */
	public static final String I0005 = "I-0005";
	
	/**
	 * 生成したユーザと違うユーザが更新等を行った場合
	 */
	public static final String I0006 = "I-0006";
	
	/**
	 * 権限がないため、修正等を行えなかった場合
	 */
	public static final String I0007 = "I-0007";
	
	/**
	 * 予期せぬエラー
	 */
	public static final String E0002 = "E-0002";

	private static final long serialVersionUID = 1L;

	private HttpStatus status;

	private String title;

	private String message;
	
	public static AzusatoException createI0005Error(Locale locale, MessageSource messageSource, String tableNameKey) {
		String tableName = messageSource.getMessage(tableNameKey, null, locale);
		return new AzusatoException(HttpStatus.BAD_REQUEST, AzusatoException.I0005,
				messageSource.getMessage(AzusatoException.I0005, new String[] { tableName }, locale));
	}
}
