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
	 * ログイン情報がない場合
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
	 * パラメータタイプミスマッチ
	 */
	public static final String I0008 = "I-0008";
	
	/**
	 * 存在しないユーザID
	 */
	public static final String I0009 = "I-0009";
	
	/**
	 * パスワードが一致しない。
	 */
	public static final String I0010 = "I-0010";
	
	/**
	 * アップロード可能な容量を超えた場合
	 */
	public static final String I0011 = "I-0011";
	
	/**
	 * 存在しないユーザタイプ
	 */
	public static final String W0001 = "W-0001";
	
	/**
	 * CSRFトークンが存在しない
	 */
	public static final String W0002 = "W-0002";
	
	/**
	 * 予期せぬエラー
	 */
	public static final String E0001 = "E-0001";
	
	/**
	 * 指定したパスにファイルが存在しないため、読み込み失敗
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
