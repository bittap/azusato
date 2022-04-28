package com.my.azusato.api.service.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class AddNonMemberUserServiceAPIRequest {

	String name;
	
	String profileImageType;
	
	String profileImageBase64;
	
	String id;
}
