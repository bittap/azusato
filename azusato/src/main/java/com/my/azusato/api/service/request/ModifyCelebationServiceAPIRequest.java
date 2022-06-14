package com.my.azusato.api.service.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class ModifyCelebationServiceAPIRequest {

	Long userNo;
	
	Long celebationNo;
	
	String title;

	String content;

	String name;
}
