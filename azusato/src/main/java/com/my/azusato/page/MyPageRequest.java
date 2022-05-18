package com.my.azusato.page;

import com.my.azusato.api.controller.MyPageControllerRequest;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Data
@Builder
public class MyPageRequest {

	/**
	 * 現在ページ番号。
	 * 注意:0から始まらない。
	 */
	private int currentPageNo;
	
	/**
	 * ページのElement表示数
	 */
	private int pageOfElement;
	
	/**
	 * ページに該当するページリスト ex) 1, 2, 3
	 */
	private int pagesOfpage;
	
	/**
	 * 総Element数
	 */
	private long totalElements;
	
	/**
	 * {@link MyPageControllerRequest}を{@link MyPageRequest}に変更
	 * @param pageReq {@link MyPageControllerRequest}
	 * @param totalElements 総Element数
	 * @return {@link MyPageRequest}
	 */
	public static MyPageRequest of(MyPageControllerRequest pageReq, long totalElements) {
		return MyPageRequest.builder()
					.currentPageNo(pageReq.getCurrentPageNo())
					.pageOfElement(pageReq.getPageOfElement())
					.pagesOfpage(pageReq.getPagesOfpage())
					.totalElements(totalElements)
					.build();
					
	}
}
