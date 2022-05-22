package com.my.azusato.api.controller.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Data
@Builder
public class GetCelebrationsAPIRequset {

	private MyPageControllerRequest page;
}
