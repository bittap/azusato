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
public class GetCelebrationNoticesSerivceAPIResponse {
	
	private List<Notice> notices;
	
	@Data
	@AllArgsConstructor
	@Builder
	@NoArgsConstructor
	public static class Notice{
		private String name;
		
		private String title;
		
		private LocalDateTime createdDatetime;

		private Long celebrationNo;
		
		private Long celebrationReplyNo;
		
		private String profileImagePath;
		
		private Boolean readed;
	}
	

	
	/**
	 * 既読していない数。navアイコンに表示すうｒ。
	 */
	private Integer noReadLength;
}
