package com.my.azusato.property;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * properties relate to profile. prefix = profile
 * 
 * @author kim-t
 *
 */
@ConfigurationProperties(prefix = "profile")
@ConstructorBinding
@AllArgsConstructor
@Getter
public class ProfileProperty {
	/**
	 * サーバーからするとの基本イメージの格納場所
	 */
	private final String serverDefaultImageFolderPath;
	
	/**
	 * クライアントからするとの基本イメージの格納場所
	 */
	private final String clientDefaultImageFolderPath;
	
	/**
	 * クライアントの本イメージのファイルの格納場所
	 */
	private final String clientImageFolderPath;
}
