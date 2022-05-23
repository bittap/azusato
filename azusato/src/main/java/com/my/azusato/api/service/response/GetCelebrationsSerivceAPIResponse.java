package com.my.azusato.api.service.response;

import java.time.LocalDateTime;
import java.util.List;

import com.my.azusato.page.MyPageResponse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class GetCelebrationsSerivceAPIResponse {
	
	private List<Celebration> celebrations;
	
	/**
	 * ページ情報
	 */
	private MyPageResponse page;
	
	@Data
	@AllArgsConstructor
	@Builder
	@NoArgsConstructor
	public static class Celebration{
		private String title;
		
		private String name;
		
		private String profileImageType;
		
		private String profileImageBase64;
		
		private Long no;
		
		private LocalDateTime createdDatetime;
	}
}
