PagingUi = function(){

}


PagingUi.prototype.create = function(page,currentPageNo,clickEvent){
	this.removePageItemWrap();
	this.initPrePage(page.pages,page.hasPrevious);
	this.initNextPage(page.pages,page.hasNext);
	this.initPages(page.pages,currentPageNo);

	this.setClickEvent(clickEvent);
}


PagingUi.prototype.initPrePage = function(pages,hasPrevious){
	const PRE_PAGE_WRAP_ELE = document.querySelector('.page-pre_wrap');
	const PRE_PAGE_ELE = document.querySelector('.page-pre');
	
	if(hasPrevious == false){
		PRE_PAGE_WRAP_ELE.classList.add('disabled');
		PRE_PAGE_ELE.setAttribute("tabindex","-1");
	}else{
		PRE_PAGE_WRAP_ELE.classList.remove('disabled');
		PRE_PAGE_ELE.removeAttribute("tabindex");
		// 以前ページのページの番号
		// 一番左のリストから-1
		const PRE_PAGE_NO = pages[0] - 1;
		// URL設定
		PRE_PAGE_ELE.setAttribute(PAGE_NO_ATTRIBUTE_NAME,PRE_PAGE_NO);
	}
}

PagingUi.prototype.initNextPage = function(pages,hasNext){
	const NEXT_PAGE_WRAP_ELE = document.querySelector('.page-next_wrap');
	const NEXT_PAGE_ELE = document.querySelector('.page-next');
	
	if(hasNext == false){
		NEXT_PAGE_WRAP_ELE.classList.add('disabled');
		NEXT_PAGE_ELE.setAttribute("tabindex","-1");
	}else{
		NEXT_PAGE_WRAP_ELE.classList.remove('disabled');
		NEXT_PAGE_ELE.removeAttribute("tabindex","-1");
		// 次ページのページの番号
		// 一番右のリストから+1
		const NEXT_PAGE_NO = pages.slice(-1)[0] + 1;
		// URL設定
		NEXT_PAGE_ELE.setAttribute(PAGE_NO_ATTRIBUTE_NAME,NEXT_PAGE_NO);
	}
}

PagingUi.prototype.initPages = function(pages,currentPageNo){
	const PAGE_ITEM_WRAP = document.querySelector('.page-item_wrap');
	const TEMP_PAGE_ITEM = document.querySelector('#template-page-item');
	
	pages.forEach((e)=>{
		const CLONED_PAGE_ITEM = TEMP_PAGE_ITEM.content.cloneNode(true);
		const liEle = CLONED_PAGE_ITEM.querySelector('li');
		const targetEle = liEle.querySelector('button');
		
		targetEle.setAttribute(PAGE_NO_ATTRIBUTE_NAME,e);
		targetEle.textContent = e;
		if(e == currentPageNo){
			liEle.classList.add('active');
		}

		PAGE_ITEM_WRAP.appendChild(CLONED_PAGE_ITEM);
	})
}

PagingUi.prototype.setClickEvent = function(clickEventForActivation){
	const pageButtons = document.querySelectorAll('.pagination li button');
	pageButtons.forEach(pageButton => {
		// 既存のクリックEvent削除
		let pageButtonJquery = $(pageButton);
		$(pageButtonJquery).off("click");
		pageButtonJquery.on('click',clickEventForActivation);
	});
}

PagingUi.prototype.removePageItemWrap = function(){
	const PAGE_ITEM_WRAP = document.querySelector('.page-item_wrap');
	
	PAGE_ITEM_WRAP.querySelectorAll('li').forEach(e=>e.remove());
}