package com.my.azusato.api.controller.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Data
@Builder
public class AddNonMemberUserAPIRequest {

	String name;

	String profileImageType;

	String profileImageBase64;
}
