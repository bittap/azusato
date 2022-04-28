package com.my.azusato.property;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

import lombok.AllArgsConstructor;
import lombok.Getter;

@ConfigurationProperties(prefix = "user")
@ConstructorBinding
@AllArgsConstructor
@Getter
public class UserProperty {
	/**
	 * length of id for nonmember
	 */
	private final Integer nonMemberIdLength;
	/**
	 * max-age of cookie 10 years
	 */
	private final Integer nonMemberCookieMaxTime;

}
