package com.my.azusato.api.controller.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import com.my.azusato.common.RegexConstant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Data
@Builder
public class AddNonMemberUserAPIRequest {

	@NotBlank(message = "{notBlank}")
	@Size(max = 10, message = "{size-string.max}")
	String name;

	@Pattern(regexp = RegexConstant.profileImageBase64, message = "{profileImageBase64.regex}")
	String profileImageType;

	String profileImageBase64;
}
