// リストページのURL(クエリパラメータなし)
// 遷移のため使う
const CELEBRATION_LIST_URL = "/" + language + "/" + "celebration";
//　お祝い書き込みtemplateが挿入される位置
//const replyContainer = document.querySelector('.reply');
// URLSearchParamsを取得
const urlParams = urlCommon.urlParams;
// お祝いの参照番号に対する属性名
const CELBRATION_NO_DATA_ATTRIBUTE_NAME = "data-celebration-no";
// お祝いの書き込み参照番号に対する属性名
const CELBRATION_REPLY_NO_DATA_ATTRIBUTE_NAME = "data-celebration-reply-no";
// ログインしたユーザの情報
let userRes;

const initialize = function(){
	getCelebrations().then(result =>{
		console.log("お祝いリスト結果",result);
		// コンテンツのtemplate
		const tempContent = document.querySelector('#template-list');
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
					//console.log("toggle close");
					bsCollapse.toggle();
				}else{
					//console.log("toggle open");
					
					// 参照数アップ
					// これはエラーが起きてもコンテンツ表示に影響はないように。
					readCountUp(celebation.no);
					
					/*
					 * toggleされると表示されるとコンテンツを表示する。
					 */
					try{
						// 一回contentAPIが実行されたら、これをクラスに追加し二回呼び出さないようにする。
						const INVOKED_COTENT_CLASS_NAME = "invoked_content";
						
						// 一回コンテンツ取得APIを実行していない場合のみ、実行
						if(!bsCollapse._element.classList.contains(INVOKED_COTENT_CLASS_NAME)){
							const contents = await getCelebrationContent(celebation.no);
							initContentArea(contents,bsCollapse._element);
							bsCollapse._element.classList.add(INVOKED_COTENT_CLASS_NAME);
						}
						bsCollapse.toggle();
					}catch(e){
						console.log(e)
						modalCommon.displayErrorModal(e.title,e.message);
					}
				}
			});
			
			// コンテンツbodyをクリックするとコンテンツ領域を非表示にする
			toggleContentWrap_tag.addEventListener('click',function(){
				bsCollapse.hide();
			});
			// お祝いfragmentに追加
			document.querySelector('#list-container').appendChild(celebrationClone);
		});
		// ページング
		paging(result.page);
		// ユーザ情報を取得しておく
		userInfo();
	}).catch(e =>{
		console.log(e);
		modalCommon.displayErrorModal(e.title,e.message);
	});
}

/*
 * ユーザ情報を取得する。
 * 現在はログインした場合、書き込みに名前を入れておくために使う。
 */
const userInfo = async function(){
	console.log("ログイン有無確認API");
	const res = await fetch(apiUrl+"/session/checked-login-session");

	const result = await res.json();
	
	if(!res.ok) {
		return Promise.reject(result);
	}else{
		// セッションがある場合
		if(Boolean(result) == true){
			userRes = await getUser();
		}
	}
}

/*
 * ユーザ情報を取得
 */
const getUser = async function(){
	console.log("ユーザ情報取得");
	const res = await fetch(apiUrl+"/user");
	
	const result = await res.json();
	
	if(!res.ok) {
		return Promise.reject(result);
	}else{
		return Promise.resolve(result);
	}
}
/*
 * コンテンツエリアを表示する。
 * @param {object} contents contentAPIによる戻り値
 * @param {document} toggledTag クリックEventによる表示されるコンテンツ領域のdocument
 */
const initContentArea = async function(contents , toggledTag ){
	const BODY_TAG = toggledTag.querySelector(".-body");
	const MODIFY_BTN_TAG = toggledTag.querySelector(".-modify");
	const DELETE_BTN_TAG = toggledTag.querySelector(".-delete");
	
	
	BODY_TAG.innerHTML = contents.content;
	
	if(contents.owner){
		// 属性追加
		MODIFY_BTN_TAG.setAttribute(CELBRATION_NO_DATA_ATTRIBUTE_NAME, contents.no);
		MODIFY_BTN_TAG.addEventListener('click',moveModify);
		
		DELETE_BTN_TAG.setAttribute(CELBRATION_NO_DATA_ATTRIBUTE_NAME, contents.no);
		DELETE_BTN_TAG.addEventListener('click',function(){
			modalCommon.displayTwoBtnModal(DELETE_MODAL_TITLE,DELETE_MODAL_BODY,function(){
				deleteCelebration(DELETE_BTN_TAG);
			});
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
			REPLY_DELETE_BTN_TAG.setAttribute(CELBRATION_REPLY_NO_DATA_ATTRIBUTE_NAME, reply.no);
			REPLY_DELETE_BTN_TAG.addEventListener('click',function(){
				modalCommon.displayTwoBtnModal(DELETE_MODAL_TITLE,DELETE_MODAL_BODY,function(){
					deleteCelebrationReply(REPLY_DELETE_BTN_TAG);
				});
			});
		}else{
			REPLY_DELETE_BTN_TAG.remove();
		}

		REPLY_FRAGMENT.appendChild(celebrationReplyClone);
	});
	
	const nameInput = toggledTag.querySelector('input[name="name"]');
	nameInput.value = userRes.name;
	// 属性追加
	REPLY_WRITE_BTN_TAG.setAttribute(CELBRATION_NO_DATA_ATTRIBUTE_NAME, contents.no);
	REPLY_WRITE_BTN_TAG.addEventListener('click',async function(){
		try{
			await addCelebrationReply(REPLY_WRITE_BTN_TAG);
		}catch(e){
			console.log(e);
			modalCommon.displayErrorModal(e.title,e.message);
			
		}
	});
	
	// 書き込みエリアに挿入
	toggledTag.querySelector("#reply-container").appendChild(REPLY_FRAGMENT);
	
}

/*
 * 書き込みを追加する。 
 */
const addCelebrationReply = async function(clickedEle){
	console.log("書き込み追加API");
	
	if(clickedEle.getAttribute(CELBRATION_NO_DATA_ATTRIBUTE_NAME) == null){
		consoel.log(`この書き込み追加ボタンに「${CELBRATION_NO_DATA_ATTRIBUTE_NAME}」属性の値が存在しません。`);
		modalCommon.displayErrorModal();
	}else{
		const celebrationNo = clickedEle.getAttribute(CELBRATION_NO_DATA_ATTRIBUTE_NAME);
		
		const res = await fetch(apiUrl+"/celebration-reply/"+celebrationNo,{
			method: 'POST',
			headers: {
			  'Accept': 'application/json',
			  'Content-Type': 'application/json'
			},
			 body: JSON.stringify({
				 name: clickedEle.parentNode.querySelector('[name="name"]').value,
				 content:  clickedEle.parentNode.querySelector('[name="content"]').value
			})
		});

		
		if(!res.ok) {
			const result = await res.json();
			return Promise.reject(result);
		}else{
			location.href = createNowPageUrl(getCurrentPageno());
		}
		
	}
}

/*
 * 修正ボタンを押下した場合の関数
 * 修正ページに移動させる。
 * 
 * @throw 指定の属性にcelebrationNoが存在しない。
 */
const moveModify = function(){
	const clickedModifyEle = this;
	
	if(clickedModifyEle.getAttribute(CELBRATION_NO_DATA_ATTRIBUTE_NAME) == null){
		consoel.log(`この修正ボタンに「${CELBRATION_NO_DATA_ATTRIBUTE_NAME}」属性の値が存在しません。`);
		modalCommon.displayErrorModal();
	}else{
		const celebrationNo = clickedModifyEle.getAttribute(CELBRATION_NO_DATA_ATTRIBUTE_NAME);
		
		location.href = `/${language}/celebration/write/${celebrationNo}`; 
	}
}

/*
 * お祝いデータを削除する。
 * 削除が成功すると現在ページを持つリストページに遷移させる。
 */
const deleteCelebration = async function(clickedDeleteEle){
	if(clickedDeleteEle.getAttribute(CELBRATION_NO_DATA_ATTRIBUTE_NAME) == null){
		consoel.log(`この削除ボタンに「${CELBRATION_NO_DATA_ATTRIBUTE_NAME}」属性の値が存在しません。`);
		modalCommon.displayErrorModal();
	}else{
		try{
			const celebrationNo = clickedDeleteEle.getAttribute(CELBRATION_NO_DATA_ATTRIBUTE_NAME);
			
			const res = await fetch(apiUrl+"/celebration/" + celebrationNo,{
				method: 'DELETE',
				headers: {
				  'Accept': 'application/json',
				  'Content-Type': 'application/json'
				}
			});
			
			if(!res.ok) {
				const result = await res.json();
				throw Error(result);
			}else{
				location.href = createNowPageUrl(getCurrentPageno());
			}
		}catch(e){
			console.log(e);
			modalCommon.displayErrorModal(e.title,e.message);
		}
		
	}
}

/*
 * お祝い書き込みデータを削除する。
 * 削除が成功すると現在ページを持つリストページに遷移させる。
 */
const deleteCelebrationReply = async function(clickedEle){
	if(clickedEle.getAttribute(CELBRATION_REPLY_NO_DATA_ATTRIBUTE_NAME) == null){
		consoel.log(`この書き込み削除ボタンに「${CELBRATION_REPLY_NO_DATA_ATTRIBUTE_NAME}」属性の値が存在しません。`);
		modalCommon.displayErrorModal();
	}else{
		try{
			const celebrationNo = clickedEle.getAttribute(CELBRATION_REPLY_NO_DATA_ATTRIBUTE_NAME);
			
			const res = await fetch(apiUrl+"/celebration-reply/" + celebrationNo,{
				method: 'DELETE',
				headers: {
				  'Accept': 'application/json',
				  'Content-Type': 'application/json'
				}
			});
			
			if(!res.ok) {
				const result = await res.json();
				throw Error(result);
			}else{
				location.href = createNowPageUrl(getCurrentPageno());
			}
		}catch(e){
			console.log(e);
			modalCommon.displayErrorModal(e.title,e.message);
		}
		
	}
}

/*
 * リクエストのページング情報により、ページングをレンダリングする。
 * @param {object} page お祝いリスト取得によるページング情報
 */
const paging = function(page){
	/*
	 * ページングに関するDomElement
	 */
	const PRE_PAGE_WRAP_ELE = document.querySelector('.page-pre_wrap');
	const NEXT_PAGE_WRAP_ELE = document.querySelector('.page-next_wrap');
	const PRE_PAGE_ELE = document.querySelector('.page-pre');
	const NEXT_PAGE_ELE = document.querySelector('.page-next');
	const PAGE_ITEM_WRAP = document.querySelector('.page-item_wrap');
	const TEMP_PAGE_ITEM = document.querySelector('#template-page-item');
	
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
		const NEXT_PAGE_NO = page.pages.slice(-1)[0] + 1;
		// URL設定
		NEXT_PAGE_ELE.href = createNowPageUrl(NEXT_PAGE_NO);
	}
	
	page.pages.forEach((e)=>{
		const CLONED_PAGE_ITEM = TEMP_PAGE_ITEM.content.cloneNode(true);
		const liEle = CLONED_PAGE_ITEM.querySelector('li');
		const aEle = liEle.querySelector('a');
		
		aEle.href = createNowPageUrl(e);
		aEle.textContent = e;
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

/*
 * 参照数アップ
 */
const readCountUp = function(celebrationNo){
	console.log("お祝い参照数アップ");
	
	fetch(apiUrl+"/celebration/read-count-up/" + celebrationNo,{
		method: 'PUT',
		headers: {
		  'Accept': 'application/json',
		  'Content-Type': 'application/json'
		}
	});
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