// URLSearchParamsを取得
const urlParams = urlCommon.urlParams;

const initEvent = function(){
	const radios = document.querySelectorAll('.search-wrap input[type="radio"]');
	radios.forEach(radio => {
		allowUncheck(radio);
		addSearchClickEvent(radio);
	})
}

const initContent = async function(searchParams){
	getList(searchParams).then(result => {
		console.log("結果",result);
		// コンテンツのtemplate
		const tempContent = document.querySelector('#template-list');
		tempContent.weddingAttenders.forEach((weddingAttender) => {
			const clone = tempContent.content.cloneNode(true);
			
			/*
			 * タグ
			 */
			const NO_TAG = clone.querySelector(".-no");
			const NAME_TAG = clone.querySelector(".-name");
			const NATION_TAG = clone.querySelector(".-nationality");
			const ATTEND_TAG = clone.querySelector(".-attend");
			const EATTING_TAG = clone.querySelector(".-eatting");
			const CREATED_DATETIME_TAG = clone.querySelector(".-created_datetime");
			const REMARK_WRAP_TAG = clone.querySelector(".remark-wrap");
			const REMARK_TAG = clone.querySelector(".-remark");
			
			NO_TAG.textContent = weddingAttender.no;
			NAME_TAG.textContent = weddingAttender.name;
			NATION_TAG.textContent = getNationText(weddingAttender.nationality);
			ATTEND_TAG.textContent = getAttendText(weddingAttender.attend);
			EATTING_TAG.textContent = getEattingText(weddingAttender.eatting);
			CREATED_DATETIME_TAG.textContent = getCreatedDatetimeText(weddingAttender.createdDatetime);
			
			if(isNoRemark(weddingAttender.remark)){
				REMARK_WRAP_TAG.style.display = none;
			}else{
				REMARK_TAG.textContent = weddingAttender.remark;
			}
			
			// TODO
			paging();
			
		})
	})
}

const getNationText = function(nationality){
	switch (nationality) {
	case "KOREA":
		return NATIONALITY_KOREA_TEXT;
	case "JAPAN":
		return NATIONALITY_JAPAN_TEXT;
	case "ETC":
		return NATIONALITY_ETC_TEXT;
	default:
		throw new Error(`[$nationality]は異常な国籍`);
	}
}

const getAttendText = function(attend){
	return attend ? '〇' : '✖';
}

const getEattingText = function(eatting){
	return eatting ? '〇' : '✖';
}

const getCreatedDatetimeText = function(createdDatetime){
	return moment(new Date(createdDatetime)).format(DATETIME_FORMAT);
}

const isNoRemark = function(remark){
	return remark != null ? true : false; 
}

const getList = async function(searchParams){
	console.log("参加情報取得");
	const currentPageNo = getCurrentPageno();
	const urlParams =  new URLSearchParams({
		"nationality": searchParams?.nationality,
		"attend": searchParams?.attend,
		"eatting": searchParams?.eatting,
		"division": searchParams?.division,
		"remarkNonNull": searchParams?.remarkNonNull,
		"offset": PAGE_PAGES_OF_PAGE*(currentPageNo-1),
		"limit": PAGE_PAGES_OF_ELEMENT
	});
	console.log("urlParams:"+urlParams);
	
	const res = await fetch(apiUrl+"/wedding/attenders?" + urlParams);
	
	const result = await res.json();
	
	if(!res.ok) {
		return Promise.reject(result);
	}else{
		return result;
	}
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
 * 活性しているラジオボタンを押下した場合、非活性させる機能を追加
 */
const allowUncheck = function(radio){
	radio.addEventListener('click',function(e){
		if(this.previous){
			this.checked = false;
		}
		this.previous = this.checked;
	});
}

const addSearchClickEvent = function(radio){
	radio.addEventListener('click',function(){
		search();
	});
}

const search = function(){
	const searchParams = {
		"nationality": document.querySelector('input[name="nationality"]:checked')?.value,
		"attend": document.querySelector('input[name="attend"]:checked')?.value,
		"eatting": document.querySelector('input[name="eatting"]:checked')?.value,
		"division": document.querySelector('input[name="division"]:checked')?.value,
		"remarkNonNull": document.querySelector('input[name="remarkNonNull"]:checked')?.value,	
	}
	
	initContent(searchParams);
}

initEvent();
initContent();