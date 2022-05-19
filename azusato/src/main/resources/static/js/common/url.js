/*
 * URLに関する共通関数
 */


/*
 * クエリパラメータを通じてURLSearchParamsを取得する。
 */
UrlCommon = function(){
	const queryString = window.location.search;
	this.urlParams = new URLSearchParams(queryString);
}