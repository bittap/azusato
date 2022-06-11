package com.my.azusato.api.service.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class GetCelebrationSerivceAPIResponse {
	
	private Long celebrationNo;
	
	private String title;
	
	private String content;
	
	private String name;
	
	private String profileImagePath;
}
