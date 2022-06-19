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
@ConfigurationProperties(prefix = "celebration")
@ConstructorBinding
@AllArgsConstructor
@Getter
public class CelebrationProperty {
	/**
	 * お祝いテーブルのコンテンツパスが保持されるサーバーのフォルダ位置
	 */
	private final String serverContentFolderPath;
	
	/**
	 * 拡張子
	 */
	private final String contentExtentions;
}
