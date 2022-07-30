/*
*	/layout/nav.html関連JS
*/

// お祝いメニューは常にツールチップを表示するように
const NAV_CELEBRATION_TOOLTIP = new bootstrap.Tooltip(document.querySelector('#nav_celebration'), {
	trigger:"manual", // プログラムでツールチップが表示されるように
	offset:"0,3",
	container: 'body',
	placement: 'bottom',
});
console.log(NAV_CELEBRATION_TOOLTIP);
NAV_CELEBRATION_TOOLTIP.show();


/*
* お祝い通知リストを描く。
* 既存に表示されているリストは削除し再読み込み。
*/
const GET_NAV_CELEBRATION_NOTICES = async function(){
	try{
		let result = await GET_CELEBRATION_NOTICES();
		// リストが存在しないと処理終了
		if(result.length == 0)
			return;
			
		let noticeList = document.querySelector('.notice-list');
		const NOTICES_INSERTED_ELEMENT = document.querySelector('#notice-list-container');
		// 内部削除
		NOTICES_INSERTED_ELEMENT.innerHTML = "";
		result.notices.forEach(notice =>{
			const CLONE_NOTICE_LIST = noticeList.cloneNode(true);
		
			let celebrationListUrl = `/${language}/celebration/redirect/list/from-notice/${notice.celebrationNo}`;
			
			CLONE_NOTICE_LIST.querySelector('.notice-list-anchor').setAttribute('href',celebrationListUrl);
			CLONE_NOTICE_LIST.querySelector('.-profile').setAttribute('src',notice.profileImagePath);
			let kind = notice.celebrationReplyNo != null ? 
				CELEBRATION_NOTICE_KIND_CELEBRATION_REPLY : CELEBRATION_NOTICE_KIND_CELEBRATION;
			CLONE_NOTICE_LIST.querySelector('.-kind').textContent = kind;
			CLONE_NOTICE_LIST.querySelector('.-createdDateTime').textContent = moment(new Date(notice.createdDatetime)).format(DATETIME_FORMAT);
			CLONE_NOTICE_LIST.querySelector('.-name').textContent = notice.name;
			CLONE_NOTICE_LIST.querySelector('.-title').textContent = notice.title;
			
			// 既読の場合
			if(notice.readed) {
				addBackground(CLONE_NOTICE_LIST);
				
			}
			
			NOTICES_INSERTED_ELEMENT.appendChild(CLONE_NOTICE_LIST);
		});
		document.querySelector('#celebration_notice_count').textContent = result.noReadLength == 0 ? '0' : result.noReadLength + '+';
	}catch(e){
		console.log(e);
		modalCommon.displayErrorModal(e.title,e.message);
	}	
}

/*
 * 既に既読した場合は、クラスを追加しバックグラウンドカラーを変更
 */
const addBackground = function(CLONE_NOTICE_LIST){
	CLONE_NOTICE_LIST.classList.add("bg-gray");
	CLONE_NOTICE_LIST.querySelector('.-kind').classList.add("text-secondary");
	CLONE_NOTICE_LIST.querySelector('.-createdDateTime').classList.add("text-secondary");
	CLONE_NOTICE_LIST.querySelector('.-name').classList.add("text-secondary");
	CLONE_NOTICE_LIST.querySelector('.-title').classList.add("text-secondary");
}

/*
* お祝い通知リストを呼び出す。
*
*/
const GET_CELEBRATION_NOTICES = async function(){
	const res = await fetch(apiUrl+"/celebration-notices?" + new URLSearchParams({
		"currentPageNo": 1,
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