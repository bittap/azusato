package com.my.azusato.api.controller.request;

import com.my.azusato.api.controller.MyPageControllerRequest;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Data
@Builder
public class GetCelebrationsAPIRequset {

	private MyPageControllerRequest page;
}
