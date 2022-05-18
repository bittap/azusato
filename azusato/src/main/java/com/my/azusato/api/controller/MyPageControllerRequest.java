package com.my.azusato.api.controller;

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
	@Min(value = 1, message = "{size-number.min}")
	@NotNull(message = "{notBlank}")
	private Integer pagesOfpage;
	
	/**
	 * ページのElement表示数
	 */
	@Min(value = 1, message = "{size-number.min}")
	@NotNull(message = "{notBlank}")
	private Integer pageOfElement;
}
