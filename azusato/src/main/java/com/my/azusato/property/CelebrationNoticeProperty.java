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
@ConfigurationProperties(prefix = "celebration-notice")
@ConstructorBinding
@AllArgsConstructor
@Getter
public class CelebrationNoticeProperty {
	/**
	 * ページのリスト表示数
	 */
	private final Integer pageOfElement;
}
