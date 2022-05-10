package com.my.azusato.property;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * properties relate to rest. prefix = rest
 * 
 * @author kim-t
 *
 */
@ConfigurationProperties(prefix = "rest")
@ConstructorBinding
@AllArgsConstructor
@Getter
public class RestProperty {
	/**
	 * RestAPIの基本パス。FullURIがない場合割り当てられる。
	 */
	private final String defaultUri;
}
