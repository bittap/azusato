package com.my.azusato.api.controller.request;

import javax.validation.constraints.Pattern;

import com.my.azusato.common.RegexConstant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Data
@Builder
public class AddNonMemberUserAPIRequest {

	String name;

	@Pattern(regexp = RegexConstant.profileImageBase64, message = "{profileImageBase64.regex}")
	String profileImageType;

	String profileImageBase64;
}
