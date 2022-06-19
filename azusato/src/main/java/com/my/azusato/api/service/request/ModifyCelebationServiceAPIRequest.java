package com.my.azusato.api.service.request;

import java.io.InputStream;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class ModifyCelebationServiceAPIRequest {

	private Long userNo;
	
	private Long celebationNo;
	
	private String title;

	private InputStream content;

	private String name;
}
