package com.my.azusato.property;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 
 * @author kim-t
 *
 */
@ConfigurationProperties(prefix = "cookie")
@ConstructorBinding
@AllArgsConstructor
@Getter
public class CookieProperty {
	
	/**
	 * 基本クッキーが反映されるパス
	 */
	private final String path;
	/**
	 * ッキーを保持する期間「30日」
	 */
	private final Integer cookieMaxTime;
	
	/**
	 * ログインページでID保存ボタン押下後、ログインした時のIDを保持するクッキーのネーム
	 */
	private final String loginSaveIdName;
}
