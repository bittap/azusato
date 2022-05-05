package com.my.azusato.property;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * properties relate to session. prefix = session
 * 
 * @author kim-t
 *
 */
@ConfigurationProperties(prefix = "session")
@ConstructorBinding
@AllArgsConstructor
@Getter
public class SessionProperty {
	/**
	 * セッション維持時間
	 */
	private final Integer maxIntervalSeconds;
}
