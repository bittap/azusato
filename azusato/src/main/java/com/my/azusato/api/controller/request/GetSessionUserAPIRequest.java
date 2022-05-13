package com.my.azusato.api.controller.request;

import javax.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Data
@Builder
public class GetSessionUserAPIRequest {

	@NotBlank(message = "{notBlank}")
	Long no;
}
