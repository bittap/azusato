package com.my.azusato.page;

import java.util.LinkedList;
import java.util.List;

public interface MyPaging {

	/**
	 * ページに関する結果を返す。
	 * @param page ページレスポンスを取得するため必要な情報
	 * @return ページに関する結果
	 * @throws IllegalArgumentException パラメータエラー
	 */
	static <T> MyPageResponse of(MyPageRequest pageReq) {
		if(pageReq.getCurrentPageNo() <= 0) {
			throw new IllegalArgumentException("現在ページ番号は0以外になれません。");
		}
		
		if(pageReq.getPagesOfpage() <= 0  ) {
			throw new IllegalArgumentException("ページリストは0以外になれません。");
		}
		
		if(pageReq.getPageOfElement() <= 0  ) {
			throw new IllegalArgumentException("ページのElement表示数は0以外になれません。");
		}
		
		if(pageReq.getTotalElements() < 0  ) {
			throw new IllegalArgumentException("総Element数はマイナスになれません。");
		}

		int totalPage = (int)Math.ceil((double)pageReq.getTotalElements() / pageReq.getPageOfElement());
		
		if(totalPage == 0) {
			totalPage = 1;
		}
		
		MyPageResponse response = MyPageResponse.builder()
					.pages(getPages(pageReq.getCurrentPageNo(),pageReq.getPagesOfpage(),totalPage))
					.hasNext(hasNext(pageReq.getCurrentPageNo(), totalPage, pageReq.getPagesOfpage()))
					.hasPrivious(pageReq.getCurrentPageNo() <= pageReq.getPagesOfpage() ? false : true)
					.currentPageNo(pageReq.getCurrentPageNo())
					.totalPage(totalPage)
					.build();
		return response;
	}
	
	/**
	 * 次へボタン押下できるかどうかを判定。現在ページ番号が最後のグループの場合は押下不可能。
	 * @param currentPageNo 現在ページ番号
	 * @param lastPageNo 最後のページ番号
	 * @param pagesOfpage ページに該当するページリスト ex) 1, 2, 3
	 * @return true : 押下可能, false : 押下不か
	 */
	private static boolean hasNext(int currentPageNo, int lastPageNo, int pagesOfpage) {
		if(getPageGroup(currentPageNo, pagesOfpage) == getPageGroup(lastPageNo, pagesOfpage)) {
			return false;
		}else {
			return true;
		}
	}
	
	/**
	 * ページグループ番号を取得する。
	 * <ul>
	 * <li>7 8 => 3グループ</li>
	 * <li>4 5 6=> 2グループ</li>
	 * <li>1 2 3=> 1グループ</li>
	 * <ul>
	 * 	<li>(1)対象のページ番号/表示ページ数</li>
	 * 	<li>(2)1で取得した値の余りが1以上の場合(余りがある場合)は+1<br>その以外は、そのまま</li>
	 * 
	 * @param targetPage 対象のページ番号
	 * @param pagesOfpage 表示ページ数
	 * @return ページグループ番号
	 */
	private static int getPageGroup(int targetPage, int pagesOfpage) {
		int mod = targetPage / pagesOfpage;
		int mok = targetPage % pagesOfpage;

		if(mok >= 1) {
			return mod+1; 
		}else {
			return mod;
		}
	}
	
	/**
	 * 現在ページとページ表示数によりページリストを取得する。
	 * <ul>
	 *  <li>1 2 3 4 5 6 7 8 9 10の場合</li>
	 * 	<li>(1)以下の場合はエラーを起こす</li>
	 * 		<ul>
	 * 			<li>現在ページ, ページ表示数, 総ページ数が {@code <=} 0 </li>
	 * 			<li>現在ページ　> 総ページ数 </li>
	 * 		</ul>
	 * 	<li>(2)以下の場合は１を返す</li>
	 * 		<ul>
	 * 			<li>現在ページ, ページ表示数 = 1 </li>
	 * 		</ul>
	 * 	<li>(3)一番最初のページの値を取得</li>
	 * 		<ul>
	 * 			<li>現在ページの余り(現在ページ%表示ページ数)が0の場合は => 現在ページ - ページ表示数 + 1</li>
	 * 			<li>その以外は、現在ページ - (現在ページ%表示ページ数) + 1</li>
	 * 		</ul>
	 * 	<li>(4)一番最後のページの値を取得</li>
	 * 		<ul>
	 *          <li>現在ページの余り(現在ページ%表示ページ数)が0の場合は => 現在ページ</li>
	 * 			<li>その以外は、現在ページ - (現在ページ%表示ページ数) + ページ表示数</li>
	 * 		</ul>
	 * 	<li>(5)ページリストを取得する</li>
	 * 		<ul>
	 * 			<li>一番最初のページの値 ~ 一番最後のページの値 or 総ページ数</li>
	 * 		</ul>
	 * </ul>
	 * @param currentPageNo 現在ページ
	 * @param ページに該当するページリスト ex) 1, 2, 3
	 * @param totalPage 総ページ数
	 * @return ページリスト
	 * @throws IllegalArgumentException パラメータエラー
	 */
	private static List<Integer> getPages(int currentPageNo, int pagesOfpage, int totalPage){
		List<Integer> pages = new LinkedList<>();
		
		// (2)以下の場合は１を返す
		if(currentPageNo == 1 && pagesOfpage == 1) {
			pages.add(currentPageNo);
			return pages;
		}
		
		// 現在ページの余り
		final int CURRENT_PAGE_MOD = currentPageNo % pagesOfpage;
		
		// (3)一番最初のページの値を取得
		// 最初のページの番号
		final int FIRST_PAGE_NO = (CURRENT_PAGE_MOD == 0) ? currentPageNo - pagesOfpage + 1 : 
			currentPageNo - (currentPageNo%pagesOfpage) + 1;

		
		// (4)一番最後のページの値を取得
		// 最後のページの番号
		final int LAST_PAGE_NO = (CURRENT_PAGE_MOD == 0) ? currentPageNo : 
			currentPageNo - (currentPageNo%pagesOfpage) + pagesOfpage;
		//System.out.printf("START : %d, END : %d\n",FIRST_PAGE_NO, LAST_PAGE_NO);
		
		// (5)ページリストを取得する
		for(int i = FIRST_PAGE_NO; i<= LAST_PAGE_NO && i <= totalPage; i++) {
			pages.add(i);
		}
		
		return pages;
	}
	
	
}
