Paging = function(){
	
}

/*
 * ページ情報を返す。
 * @param {int} totalItemSize 項目の総数
 * @param {int} itemSizeOfPage 一つのページに表示する項目の数
 * @param {int} buttonLengthOfPage 表示するページの数
 * @param {int} currentPageNo 現在ページ番号
 * @return 
 * {int} hasPrevious 以前ページの表示有無
 * {array} pages ページ番号の配列
 * {int} hasNext 最後ページの表示有無
 */
Paging.prototype.getPaging = function(totalItemSize, itemSizeOfPage, buttonLengthOfPage, currentPageNo){
	if(itemSizeOfPage < 1 || buttonLengthOfPage < 1 || currentPageNo < 1)
		throw new Error(`itemSizeOfPage : ${itemSizeOfPage},buttonLengthOfPage : ${buttonLengthOfPage}, currentPageNo : ${currentPageNo}は1より小さくできません。`);
	const startPage = this.startPage(buttonLengthOfPage,currentPageNo);
	const endPage = this.endPage(totalItemSize,itemSizeOfPage);
	
	const pages = this.pages(startPage,endPage,buttonLengthOfPage);
	
	return {
		"hasPrevious" : this.hasPrevious(startPage),
		"pages" : pages,
		"hasNext" : this.hasNext(endPage,pages)
	};
	
}

/*
 * 最後のページ数を返す
 * (項目の総数 / 一つのページに表示する項目の数) + (項目の総数 % 一つのページに表示する項目の数 != 0 ? 1 : 0)
 * @param {int} totalItemSize 項目の総数
 * @param {int} itemSizeOfPage 一つのページに表示する項目の数
 * @return {int} 項目の総数 == 0 : 0 , その他 : (項目の総数 / 一つのページに表示する項目の数)切り上げ
 */
Paging.prototype.endPage = function(totalItemSize,itemSizeOfPage){
	return totalItemSize == 0 ? 0 : Math.ceil(totalItemSize/itemSizeOfPage);
}

/*
 * スタートページ情報を返す。
 * 取得する方法
 * 二つのパターンがある。
 * 1.現在ページ番号 % 表示するページの数 == 0
 *  ・(現在ページ番号 - 表示するページの数) + 1
 * 2.現在ページ番号 % 表示するページの数 != 0
 * 　 ・((現在ページ番号/表示するページの数) * 表示するページの数) + 1
 * @param {int} buttonLengthOfPage 表示するページの数
 * @param {int} currentPageNo 現在ページ番号
 * @return {int} 最初のページ数
 */
Paging.prototype.startPage = function(buttonLengthOfPage,currentPageNo){
	if(currentPageNo % buttonLengthOfPage == 0)
		return (currentPageNo - buttonLengthOfPage) + 1;
	else
		return ((currentPageNo/buttonLengthOfPage) * buttonLengthOfPage) + 1;
}

/*
 *  ページ番号の配列を返す。
 */
Paging.prototype.pages = function(startPage,endPage,buttonLengthOfPage){
	const pages = new Array();
	
	for (var i = startPage; i < (startPage + buttonLengthOfPage) -1 && i <= endPage ; i++) {
		pages.push(startPage++);
	}
	
	return pages;
}

/*
 * 以前のページ活性するかどうか決める。
 * 
 * @param {int} startPage スタートページ番号
 * @return {int} startPage != 1 : true , その他 : false
 */
Paging.prototype.hasPrevious = function(startPage){
	return startPage != 1 ? true : false;
}

/*
 * 最後のページ活性するかどうか決める。
 * 
 * @param {int} endPage 最後のページ番号
 * @param {array} pages 表示するページ番号の配列
 * @return {int} 一つのページに表示するページの数に最後のページが存在しない　: 活性 ,  その他 : 非活性
 */
Paging.prototype.hasNext = function(endPage,pages){
	return !pages.includes(endPage) ? true : false;
}