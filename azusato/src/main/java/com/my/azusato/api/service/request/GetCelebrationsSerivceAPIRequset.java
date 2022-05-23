package com.my.azusato.api.service.request;

import com.my.azusato.api.controller.request.MyPageControllerRequest;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class GetCelebrationsSerivceAPIRequset {

	MyPageControllerRequest pageReq;
}
