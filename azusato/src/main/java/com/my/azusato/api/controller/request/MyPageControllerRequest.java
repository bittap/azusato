package com.my.azusato.api.controller.request;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Data
@Builder
public class MyPageControllerRequest {

	/**
	 * 現在ページ番号
	 */
	private Integer currentPageNo;
	
	/**
	 * 表示ページ数
	 */
	@Min(value = 1, message = "{min}")
	@NotNull(message = "{notNull}")
	private Integer pagesOfpage;
	
	/**
	 * ページのリスト表示数
	 */
	@Min(value = 1, message = "{min}")
	@NotNull(message = "{notNull}")
	private Integer pageOfElement;
}
