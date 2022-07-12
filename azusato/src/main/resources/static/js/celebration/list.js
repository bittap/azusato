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
			// 通知処理から遷移した時、表示する時どんなお祝いを表示するか区別するために投与。
			toggleWrap_tag.setAttribute('id',celebation.no);
			const toggleContentWrap_tag = celebrationClone.querySelector(".toggl_content_wrap");

			PROFILE_TAG.src = celebation.profileImagePath;
			NO_TAG.textContent = "No."+celebation.no;
			REG_TAG.textContent = moment(new Date(celebation.createdDatetime)).format(DATETIME_FORMAT);
			
			// 管理者ログインの場合は参照数を表示
			let nameContent = "@"+celebation.name;
			if(ADMIN_LOGIN){
				nameContent += ` [${celebation.readCount}]`
			}
			NAME_TAG.textContent = nameContent;
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
		getUser();
		// パラメータの値よりfocusを行う。
		focusContent();
	}).catch(e =>{
		console.log(e);
		modalCommon.displayErrorModal(e.title,e.message);
	});
}


/*
* 
* クエリパラメータに"celebrationNo"と"celebrationReplyNo"がある場合、コンテンツを表示させfocusさせる。
* クエリパラメータに"celebrationReplyNo"がある場合は、それに該当する。書き込みをfocusさせる。
* "celebrationReplyNo"がない場合は、"celebrationNo"に該当するコンテンツのみ表示する。
* 表示が終わったら、通知テーブルの既読フラグを修正する。
*
*/
const focusContent = async function(){
	// パラメータのお祝い番号
	const PARAM_CELEBRATION_NO = urlParams.get('celebrationNo');
	if(PARAM_CELEBRATION_NO != null){
		let toggleWrap = getCelebrationToggleEle(PARAM_CELEBRATION_NO);
		// コンテンツを表示
		toggleWrap.click();
		// パラメータに書き込みがある場合
		if(urlParams.get('celebrationReplyNo') != null){
			// 書き込みをfocusする。
			let replyEle = getCelebrationReplyEle();
			replyEle.focus();
		}else{
			// お祝いをfocusする。
			toggleWrap.querySelector('.body').focus();
		}
		
		// 通知テーブルの既読フラグを修正する
	}
}

/*
 * 通知テーブルの既読フラグを修正する
 */
const readCelebrationNotice = async function(celebrationNo){
	const res = await fetch(apiUrl+"/celebration-notice/read/" + celebrationNo,{
		method: 'PUT',
		headers: apiCommon.header
	});
	
	if(!res.ok) {
		const result = await res.json();
		return Promise.reject(result);
	}else{
		// TODO お祝い通知リストを更新する。
	}
}

/*
 * お祝い番号より対象のToggleのElementを取得する。
* クラスが"toggle_wrap"のElementを全部取得し、IDがお祝い番号と一致したElementを返す。
* @param celebrationNo 探すお祝い番号
* @return 対象のToggleのElement. もしない場合はnull
*/
const getCelebrationToggleEle = function(celebrationNo){
	let toggleWraps = document.querySelectorAll('.toggle_wrap');
	
	toggleWraps.forEach(toggleWrap => {
		let id = toggleWrap.getAttribute('id');
		if(id == celebrationNo){
			return toggleWrap;
		}
	});
	
	return null;
} 

/*
 * お祝い書き込み番号より対象の書き込みElementを取得する。
* クラスが"reply-list"のElementを全部取得し、IDがお祝い書き込み番号と一致したElementを返す。
* @param celebrationNo 探すお祝い書き込み番号
* @return 対象のElement. もしない場合はnull
*/
const getCelebrationReplyEle = function(celebrationReplyNo){
	let replyLists = document.querySelectorAll('.reply-list');
	
	replyLists.forEach(replyList => {
		let id = replyList.getAttribute('id');
		if(id == celebrationReplyNo){
			return replyList;
		}
	});
	
	return null;
} 

/*
 * ユーザ情報を取得
 */
const getUser = async function(){
	console.log("ユーザ情報取得");
	
	if(Boolean(AUTHENTICATIONED) == true){
		const res = await fetch(apiUrl+"/user",{
			method: 'GET',
			headers: apiCommon.header
		});
		
		const result = await res.json();
		
		if(!res.ok) {
			return Promise.reject(result);
		}else{
			userRes = result;
		}
	}
}
/*
 * コンテンツエリアを表示する。
 * @param {object} contents contentAPIによる戻り値
 * @param {document} toggledTag クリックEventによる表示されるコンテンツ領域のdocument
 */
const initContentArea = async function(contents , toggledTag ){
	console.log("コンテンツエリア表示",contents);
	const BODY_TAG = toggledTag.querySelector(".-body");
	const MODIFY_BTN_TAG = toggledTag.querySelector(".-modify");
	const DELETE_BTN_TAG = toggledTag.querySelector(".-delete");
	
	
	BODY_TAG.innerHTML = await getContent(contents.contentPath);
	
	if(contents.owner){
		// 属性追加
		MODIFY_BTN_TAG.setAttribute(CELBRATION_NO_DATA_ATTRIBUTE_NAME, contents.no);
		MODIFY_BTN_TAG.addEventListener('click',moveModify);
		
		DELETE_BTN_TAG.setAttribute(CELBRATION_NO_DATA_ATTRIBUTE_NAME, contents.no);
		DELETE_BTN_TAG.addEventListener('click',function(){
			modalCommon.displayTwoBtnModal(DELETE_MODAL_TITLE,DELETE_MODAL_BODY,async function(){
				try{
					await deleteCelebration(DELETE_BTN_TAG);
				}catch(e){
					console.log(e);
					modalCommon.displayErrorModal(e.title,e.message);
					
				}
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
		// 通知処理から遷移した時、表示する時どんなお祝い書き込みをfocusするか区別するために投与。
		celebrationReplyClone.setAttribute('id',reply.no);
		const REPLY_PROFILE_TAG = celebrationReplyClone.querySelector(".-reply_profile");
		const REPLY_NAME_TAG = celebrationReplyClone.querySelector(".-reply_name");
		const REPLY_RAG_TAG = celebrationReplyClone.querySelector(".-reply_rag");
		const REPLY_CONTENT_TAG = celebrationReplyClone.querySelector(".-reply_content");
		const REPLY_DELETE_BTN_TAG = celebrationReplyClone.querySelector(".-reply_delete");
		
		REPLY_PROFILE_TAG.src = reply.profileImagePath;
		REPLY_NAME_TAG.textContent = reply.name;
		REPLY_RAG_TAG.textContent = moment(new Date(reply.createdDatetime)).format(DATETIME_FORMAT);
		REPLY_CONTENT_TAG.textContent = reply.content;
		
		if(reply.owner){
			REPLY_DELETE_BTN_TAG.setAttribute(CELBRATION_REPLY_NO_DATA_ATTRIBUTE_NAME, reply.no);
			REPLY_DELETE_BTN_TAG.addEventListener('click',function(){
				modalCommon.displayTwoBtnModal(DELETE_REPLY_MODAL_TITLE,DELETE_REPLY_MODAL_BODY,async function(){
					try{
						await deleteCelebrationReply(REPLY_DELETE_BTN_TAG);
					}catch(e){
						console.log(e);
						modalCommon.displayErrorModal(e.title,e.message);
					}	
				});
			});
		}else{
			REPLY_DELETE_BTN_TAG.remove();
		}

		REPLY_FRAGMENT.appendChild(celebrationReplyClone);
	});
	
	const nameInput = toggledTag.querySelector('input[name="name"]');
	if(userRes?.name != null){
		nameInput.value = userRes.name;
	}
	// 属性追加
	REPLY_WRITE_BTN_TAG.setAttribute(CELBRATION_NO_DATA_ATTRIBUTE_NAME, contents.no);
	REPLY_WRITE_BTN_TAG.addEventListener('click',async function(){
		try{
			// Loading画面を表示
			modalCommon.displayLoadingModal();
			
			// 非ログインしはユーザを登録しておく。
			if(Boolean(AUTHENTICATIONED) == false){
				await addnonMember(REPLY_WRITE_BTN_TAG);
				await profileUpload();
			}
			await addCelebrationReply(REPLY_WRITE_BTN_TAG);
		}catch(e){
			console.log(e);
			
			await asyncCommon.delay(LOADING_MODAL_DELAY);
			modalCommon.hideLoadingModal();
			
			modalCommon.displayErrorModal(e.title,e.message);
			
		}
	});
	
	// 書き込みエリアに挿入
	toggledTag.querySelector("#reply-container").appendChild(REPLY_FRAGMENT);
	
}


/*
 * プロフィールをアップロードする。
 */
const profileUpload = async function(){
	const formData = new FormData();
	const FILE_FIELD_NAME = 'profileImage';
	
	const randomImage = await getRandomImage();
	
	const blobData = await getBlobByImageUrl(randomImage.profileImagePath);
	
	formData.append(FILE_FIELD_NAME,blobData.blob,blobData.filename);
	
	const res = await fetch(apiUrl+"/profile/upload-img",{
		method: 'POST',
		headers: apiCommon.noContentTypeheader,
		body: formData
	});
	
	if(!res.ok) {
		const result = await res.json();
		return Promise.reject(result);
	}else{
		return Promise.resolve();
	}
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
			headers: apiCommon.header,
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

const addnonMember = async function(clickedEle){
	console.log("非会員ユーザ作成API");
	const res = await fetch(apiUrl+"/user/nonmember",{
		method: 'POST',
		headers: apiCommon.header,
		body: JSON.stringify({
			 name: clickedEle.parentNode.querySelector('[name="name"]').value
		})
	});
	
	if(!res.ok) {
		const result = await res.json();
		return Promise.reject(result);
	}else{
		return Promise.resolve();
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
		const celebrationNo = clickedDeleteEle.getAttribute(CELBRATION_NO_DATA_ATTRIBUTE_NAME);
		
		const res = await fetch(apiUrl+"/celebration/" + celebrationNo,{
			method: 'DELETE',
			headers: apiCommon.header
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
 * お祝い書き込みデータを削除する。
 * 削除が成功すると現在ページを持つリストページに遷移させる。
 */
const deleteCelebrationReply = async function(clickedEle){
	if(clickedEle.getAttribute(CELBRATION_REPLY_NO_DATA_ATTRIBUTE_NAME) == null){
		consoel.log(`この書き込み削除ボタンに「${CELBRATION_REPLY_NO_DATA_ATTRIBUTE_NAME}」属性の値が存在しません。`);
		modalCommon.displayErrorModal();
	}else{
		const celebrationNo = clickedEle.getAttribute(CELBRATION_REPLY_NO_DATA_ATTRIBUTE_NAME);
		
		const res = await fetch(apiUrl+"/celebration-reply/" + celebrationNo,{
			method: 'DELETE',
			headers: apiCommon.header
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
		headers: apiCommon.header
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