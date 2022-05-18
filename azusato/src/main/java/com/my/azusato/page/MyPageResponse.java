package com.my.azusato.page;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class MyPageResponse {

	/**
	 * ページ表示番号リスト。Ex) 5,4,3
	 */
	private List<Integer> pages;
	
	/**
	 * 次ページ移動が可能かどうか
	 */
	private Boolean hasNext;
	
	/**
	 * 以前ページ移動が可能かどうか
	 */
	private Boolean hasPrivious;
	
	/**
	 * 現在ページ番号
	 */
	private Integer currentPageNo;
	
	private Integer totalPage;
}
