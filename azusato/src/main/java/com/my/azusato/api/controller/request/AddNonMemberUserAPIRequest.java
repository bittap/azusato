package com.my.azusato.api.controller.request;

import org.springframework.lang.NonNull;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class AddNonMemberUserAPIRequest {

	String name;
	
	String profileImageType;
	
	String profileImageBase64;
}
