package com.my.azusato.api.service.response;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class GetCelebrationContentSerivceAPIResponse {
	
	private Long no;
	
	private Boolean owner;
	
	private String content;
	
	private List<CelebrationReply> replys;
	
	@Data
	@AllArgsConstructor
	@Builder
	@NoArgsConstructor
	public static class CelebrationReply{
		private Long no;
		
		private String content;
		
		private LocalDateTime createdDatetime;
		
		private Boolean owner;
		
		private String name;
		
		private String profileImageType;
		
		private String profileImageBase64;
		
	}
}
