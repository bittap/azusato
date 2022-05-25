// リストページのURL(クエリパラメータなし)
// 遷移のため使う
const CELEBRATION_LIST_URL = "/" + language + "/" + "celebration";
// コンテンツのtemplate
const tempContent = document.querySelector('#template-list');
// お祝いtemplateが挿入される位置
const listContainer = document.querySelector('#list-container');
//　お祝い書き込みtemplateが挿入される位置
//const replyContainer = document.querySelector('.reply');
// URLSearchParamsを取得
const urlParams = urlCommon.urlParams;
/*
 * ページングに関するDomElement
 */
const PRE_PAGE_WRAP_ELE = document.querySelector('.page-pre_wrap');
const NEXT_PAGE_WRAP_ELE = document.querySelector('.page-next_wrap');
const PRE_PAGE_ELE = document.querySelector('.page-pre');
const NEXT_PAGE_ELE = document.querySelector('.page-next');
const PAGE_ITEM_WRAP = document.querySelector('.page-item_wrap');
const TEMP_PAGE_ITEM = document.querySelector('#template-page-item');

const initialize = function(){
	getCelebrations().then(result =>{
		console.log("お祝いリスト結果",result);
		// リストが全部レンダリング後、使われる。
		const FRAGMENT = document.createDocumentFragment();
		// ページング
		paging(result.page);
		// リスト
		result.celebrations.forEach((celebation) => {
			const celebrationClone = tempContent.content.cloneNode(true);
			/*
			 * お祝い領域
			 */
			const PROFILE_TAG = celebrationClone.querySelector(".-profile");
			const NO_TAG = celebrationClone.querySelector(".-no");
			const REG_TAG = celebrationClone.querySelector(".-regdate");
			const NAME_TAG = celebrationClone.querySelector(".-name");
			const TITLE_TAG = celebrationClone.querySelector(".-title");
			const toggleWrap_tag = celebrationClone.querySelector(".toggle_wrap");
			const toggleContentWrap_tag = celebrationClone.querySelector(".toggl_content_wrap");

			imageCommon.changeImageSrcBase64(PROFILE_TAG,celebation.profileImageType,celebation.profileImageBase64);
			NO_TAG.textContent = "No."+celebation.no;
			REG_TAG.textContent = moment(new Date(celebation.createdDatetime)).format(DATETIME_FORMAT);
			NAME_TAG.textContent = "@"+celebation.name;
			TITLE_TAG.textContent = celebation.title;
			
			const toglledCollapse = celebrationClone.querySelector(".content_wrap");
			const bsCollapse = new bootstrap.Collapse(toglledCollapse, {
			  toggle: false // 呼び出す時Toggleされるかどうか。toggleは自分がするので、false
			})
			
			toggleWrap_tag.addEventListener('click',async function(){
				if(bsCollapse._element.classList.contains("show")){
					console.log("toggle close");
				}else{
					console.log("toggle open");
					
					/*
					 * toggleされると表示されるとコンテンツを表示する。
					 */
					try{
						// TODO 参照数アップ
						const contents = await getCelebrationContent(celebation.no);
						initContentArea(contents,bsCollapse._element);
					}catch(e){
						console.log(e)
						modalCommon.displayErrorModal(e.title,e.message);
					}
				}
				bsCollapse.toggle();
			});
			
			toggleContentWrap_tag.addEventListener('click',function(){
				bsCollapse.hide();
			});
			
			
			
			// お祝いfragmentに追加
			FRAGMENT.appendChild(celebrationClone);
		});
		
		listContainer.appendChild(FRAGMENT);
	}).catch(e =>{
		console.log(e);
		modalCommon.displayErrorModal(e.title,e.message);
	});
}

const initContentArea = async function(contents , toggledTag ){
	const BODY_TAG = toggledTag.querySelector(".-body");
	const MODIFY_BTN_TAG = toggledTag.querySelector(".-modify");
	const DELETE_BTN_TAG = toggledTag.querySelector(".-delete");
	
	
	BODY_TAG.innerHTML = contents.content;
	
	if(contents.owner){
		MODIFY_BTN_TAG.addEventListener('click',function(){
			console.log("content area modify click");
		});
		
		DELETE_BTN_TAG.addEventListener('click',function(){
			console.log("content area delete click");
		});
	}else{
		MODIFY_BTN_TAG.remove();
		DELETE_BTN_TAG.remove();
	}
	
	/*
	 * 書き込み領域
	 */
	// 書き込みのtemplate
	const tempReply = toggledTag.querySelector('#template-reply-list');
	const REPLY_FRAGMENT = document.createDocumentFragment();
	const REPLY_WRITE_BTN_TAG = toggledTag.querySelector(".-reply_write");
	
	contents.replys.forEach(reply=>{
		const celebrationReplyClone = tempReply.content.cloneNode(true);
		const REPLY_PROFILE_TAG = celebrationReplyClone.querySelector(".-reply_profile");
		const REPLY_NAME_TAG = celebrationReplyClone.querySelector(".-reply_name");
		const REPLY_RAG_TAG = celebrationReplyClone.querySelector(".-reply_rag");
		const REPLY_CONTENT_TAG = celebrationReplyClone.querySelector(".-reply_content");
		const REPLY_DELETE_BTN_TAG = celebrationReplyClone.querySelector(".-reply_delete");
		
		imageCommon.changeImageSrcBase64(REPLY_PROFILE_TAG,reply.profileImageType,reply.profileImageBase64);
		REPLY_NAME_TAG.textContent = reply.name;
		REPLY_RAG_TAG.textContent = moment(new Date(reply.createdDatetime)).format(DATETIME_FORMAT);
		REPLY_CONTENT_TAG.textContent = reply.content;
		
		if(reply.owner){
			REPLY_DELETE_BTN_TAG.addEventListener('click',function(){
				console.log("content reply area 削除 click");
			});
		}else{
			REPLY_DELETE_BTN_TAG.remove();
		}

		REPLY_FRAGMENT.appendChild(celebrationReplyClone);
	});
	
	REPLY_WRITE_BTN_TAG.addEventListener('click',function(){
		console.log("content reply area write click");
	});
	
	// 書き込みエリアに挿入
	toggledTag.querySelector("#reply-container").appendChild(REPLY_FRAGMENT);
	
}

/*
 * リクエストのページング情報により、ページングをレンダリングする。
 * @param {object} page お祝いリスト取得によるページング情報
 */
const paging = function(page){
	if(page.hasPrivious == false){
		PRE_PAGE_WRAP_ELE.classList.add('disabled');
		PRE_PAGE_ELE.setAttribute("tabindex","-1");
	}else{
		// 以前ページのページの番号
		// 一番左のリストから-1
		const PRE_PAGE_NO = page.pages[0] - 1;
		// URL設定
		PRE_PAGE_ELE.href = createNowPageUrl(PRE_PAGE_NO);
	}
	
	if(page.hasNext == false){
		NEXT_PAGE_WRAP_ELE.classList.add('disabled');
		NEXT_PAGE_ELE.setAttribute("tabindex","-1");
	}else{
		// 次ページのページの番号
		// 一番右のリストから+1
		const NEXT_PAGE_NO = page.slice(-1)[0] + 1;
		// URL設定
		NEXT_PAGE_ELE.href = createNowPageUrl(NEXT_PAGE_NO);
	}
	
	page.pages.forEach((e)=>{
		const CLONED_PAGE_ITEM = TEMP_PAGE_ITEM.content.cloneNode(true);
		const liEle = CLONED_PAGE_ITEM.querySelector('li');
		const aEle = liEle.querySelector('a');
		
		aEle.href = createNowPageUrl(e);
		if(e == getCurrentPageno()){
			liEle.classList.add('active');
		}
		
		PAGE_ITEM_WRAP.appendChild(CLONED_PAGE_ITEM);
	})
	
}

/*
 * パラメータによる現在ページのURLを作る
 * @param {int} currentPageNo ページ番号
 * @return {string} 現在ページのURL
 */
const createNowPageUrl = function(currentPageNo){
	return CELEBRATION_LIST_URL + "?" + new URLSearchParams({
		"currentPageNo": currentPageNo
	});
}
/*
 * 現在ページ番号を取得する。
 * クエリパラメータにない場合は1を返す。
 * @return {int} 現在ページの番号
 */
const getCurrentPageno = function(){
	return urlParams.has("currentPageNo") ? urlParams.get("currentPageNo") : 1 ;
}

const getCelebrationContent = async function(celebrationNo){
	console.log("お祝いコンテンツ取得");
	modalCommon.displayLoadingModal();
	const res = await fetch(apiUrl+"/celebration/content/" + celebrationNo);
	
	const result = await res.json();
	
	await asyncCommon.delay(LOADING_MODAL_DELAY);
	modalCommon.hideLoadingModal();
	
	if(!res.ok) {
		return Promise.reject(result);
	}else{
		console.log("お祝いコンテンツ取得 成功",result);
		return result;
	}
}

const getCelebrations = async function(){
	console.log("お祝いリスト取得");
	const currentPageNo = getCurrentPageno();
	const res = await fetch(apiUrl+"/celebrations?" + new URLSearchParams({
		"currentPageNo": currentPageNo,
		"pagesOfpage": PAGE_PAGES_OF_PAGE,
		"pageOfElement": PAGE_PAGES_OF_ELEMENT
	}));
	
	const result = await res.json();
	
	if(!res.ok) {
		return Promise.reject(result);
	}else{
		return result;
	}
}

initialize();