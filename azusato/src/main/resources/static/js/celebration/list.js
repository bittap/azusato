// コンテンツのtemplate
const tempContent = document.querySelector('#template-list');
// お祝いtemplateが挿入される位置
const listContainer = document.querySelector('#list-container');
//　お祝い書き込みtemplateが挿入される位置
//const replyContainer = document.querySelector('.reply');
// URLSearchParamsを取得
const urlParams = urlCommon.urlParams;

const initialize = function(){
	getCelebrations().then(result =>{
		console.log("お祝いリスト結果",result);
		// リストが全部レンダリング後、使われる。
		const FRAGMENT = document.createDocumentFragment();
		
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
			const BODY_TAG = celebrationClone.querySelector(".-body");
			const MODIFY_BTN_TAG = celebrationClone.querySelector(".-modify");
			const DELETE_BTN_TAG = celebrationClone.querySelector(".-delete");
			
			imageCommon.changeImageSrcBase64(PROFILE_TAG,celebation.profileImageType,celebation.profileImageBase64);
			NO_TAG.textContent = "No."+celebation.no;
			REG_TAG.textContent = moment(new Date(celebation.createdDatetime)).format(DATETIME_FORMAT);
			NAME_TAG.textContent = "@"+celebation.name;
			TITLE_TAG.textContent = celebation.title;
			BODY_TAG.innerHTML = celebation.content;
			
			if(celebation.owner){
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
			const tempReply = celebrationClone.querySelector('#template-reply-list');
			const REPLY_FRAGMENT = document.createDocumentFragment();
			const REPLY_WRITE_BTN_TAG = celebrationClone.querySelector(".-reply_write");
			
			celebation.replys.forEach(reply=>{
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
			celebrationClone.querySelector("#reply-container").appendChild(REPLY_FRAGMENT);
			// お祝いfragmentに追加
			FRAGMENT.appendChild(celebrationClone);
		});
		
		listContainer.appendChild(FRAGMENT);
	}).catch(e =>{
		console.log(e);
		modalCommon.displayApiErrorModal(e);
	});
}

const getCelebrations = async function(){
	console.log("お祝いリスト取得");
	const currentPageNo = urlParams.has("currentPageNo") ? urlParams.get("currentPageNo") : 1;
	const res = await fetch(apiUrl+"/celebration/list?" + new URLSearchParams({
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