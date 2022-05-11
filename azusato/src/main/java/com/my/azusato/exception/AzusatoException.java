package com.my.azusato.exception;

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
	public static final String E0001 = "E-0001";

	private static final long serialVersionUID = 1L;

	private HttpStatus status;

	private String title;

	private String message;
}
