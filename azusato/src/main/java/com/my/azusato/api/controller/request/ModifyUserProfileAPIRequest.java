package com.my.azusato.api.controller.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import com.my.azusato.common.RegexConstant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Data
@Builder
public class ModifyUserProfileAPIRequest {

	@NotBlank(message = "{notBlank}")
	String name;

	@Pattern(regexp = RegexConstant.profileImageBase64, message = "{profileImageBase64.regex}")
	@NotBlank(message = "{notBlank}")
	String profileImageType;

	@NotBlank(message = "{notBlank}")
	String profileImageBase64;
}
