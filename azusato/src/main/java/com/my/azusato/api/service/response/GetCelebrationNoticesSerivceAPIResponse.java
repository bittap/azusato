package com.my.azusato.api.service.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class GetCelebrationNoticesSerivceAPIResponse {
	
	private String name;
	
	private String title;
	
	private LocalDateTime createdDatetime;

	private Long celebrationNo;
	
	private Long celebrationReplyNo;
	
	private String profileImagePath;
}
