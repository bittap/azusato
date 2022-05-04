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
	 * 基本イメージの最大番号
	 */
	private final Integer defaultMaxNumber;

	/**
	 * 基本イメージのイメージタイプ
	 */
	private final String defaultImageType;
}
