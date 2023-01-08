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

UrlCommon.prototype.updateQueryParameter = function(urlString,name,value){
	let url = new URL(urlString);
	let params = new URLSearchParams(url.search);
	
	params.set(name, value);
	url.search = params;
	return url;
}