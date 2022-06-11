package com.my.azusato.api.controller.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

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
}
