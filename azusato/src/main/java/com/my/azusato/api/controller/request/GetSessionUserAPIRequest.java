package com.my.azusato.api.controller.request;

import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Data
@Builder
public class GetSessionUserAPIRequest {

	@NotNull(message = "{notNull}")
	Long no;
}
