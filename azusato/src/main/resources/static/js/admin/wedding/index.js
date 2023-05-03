// URLSearchParamsを取得
const urlParams = urlCommon.urlParams;
const PAGE_NO_ATTRIBUTE_NAME = "data-page-no";
const itemSizeOfPage = 10;
const buttonLengthOfPage = 5;
const divisionDatetime = moment(new Date(DIVISION_DATE)).format(DATETIME_FORMAT);

const initEvent = function(){
	const radios = document.querySelectorAll('.search-wrap input[type="radio"]');
	radios.forEach(radio => {
		allowUncheck(radio);
		addSearchClickEvent(radio);
	});
}

const initContent = async function(searchParams){
	document.querySelector('#list-container').innerHTML = "";
	getList(searchParams).then(result => {
		console.log("結果",result);
		// コンテンツのtemplate
		const tempContent = document.querySelector('#template-list');
		result.weddingAttenders.forEach((weddingAttender) => {
			const clone = tempContent.content.cloneNode(true);
			const TR_TAG = clone.querySelector("tr");
			if(weddingAttender.attenderNumber > 1){
				TR_TAG.style.backgroundColor = '#198754';
			}
			/*
			 * タグ
			 */
			const NO_TAG = clone.querySelector(".-no");
			const NAME_TAG = clone.querySelector(".-name");
			const NATION_TAG = clone.querySelector(".-nationality");
			const ATTEND_TAG = clone.querySelector(".-attend");
			const EATTING_TAG = clone.querySelector(".-eatting");
			const DIVISION_TAG = clone.querySelector(".-division");
			const ATTENDER_NUMBER_TAG = clone.querySelector(".-attenderNumber");
			const CREATED_DATETIME_TAG = clone.querySelector(".-created_datetime");
			const REMARK_TAG = clone.querySelector(".-remark");
			
			NO_TAG.textContent = weddingAttender.no;
			NAME_TAG.textContent = weddingAttender.name;
			NATION_TAG.textContent = getNationText(weddingAttender.nationality);
			ATTEND_TAG.textContent = getAttendText(weddingAttender.attend);
			EATTING_TAG.textContent = getEattingText(weddingAttender.eatting);
			DIVISION_TAG.textContent = getDivisionText(weddingAttender.createdDatetime);
			ATTENDER_NUMBER_TAG.textContent = weddingAttender.attenderNumber;
			CREATED_DATETIME_TAG.textContent = getCreatedDatetimeText(weddingAttender.createdDatetime);
			REMARK_TAG.textContent = weddingAttender.remark;
			
			document.querySelector('#list-container').appendChild(clone);
		})
		document.querySelector('#totalAttenderNumber').textContent = result.totalAttenderNumber;
		const pageInfo = pagingCommon.getPaging(result.total,itemSizeOfPage,buttonLengthOfPage,searchParams.currentPageNo);
		pagingUiCommon.create(pageInfo,searchParams.currentPageNo,function(){
			search(this.getAttribute(PAGE_NO_ATTRIBUTE_NAME));
		});
	}).catch(e =>{
		console.log(e);
		modalCommon.displayErrorModal(e.title,e.message);
	});
}

const getDivisionText = function(createdDatetimeString){
	const divisionDatetime = moment(new Date(DIVISION_DATE));
	const createdDatetime = moment(new Date(createdDatetimeString));
	
	return createdDatetime.isBefore(divisionDatetime) ? DIVISION_FIRST_DATE : DIVISION_SECOND_DATE;
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
	return remark != null ? false : true; 
}

const getList = async function(searchParams){
	console.log("参加情報取得");
	const urlParams =  new URLSearchParams({
		"nationality": searchParams?.nationality,
		"attend": searchParams?.attend,
		"eatting": searchParams?.eatting,
		"division": searchParams?.division,
		"remarkNonNull": searchParams?.remarkNonNull,
		"offset": getOffset(searchParams.currentPageNo),
		"limit": itemSizeOfPage
	});
	[...urlParams.entries()].forEach(([key, value]) => {
		  if (value === "undefined") {
			 urlParams.delete(key);
		  }
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
		search(1);
	});
}

const search = function(currentPageNo){
	console.log("search currentPageNo: " + currentPageNo);
	const searchParams = {
		"nationality": document.querySelector('input[name="nationality"]:checked')?.value,
		"attend": document.querySelector('input[name="attend"]:checked')?.value,
		"eatting": document.querySelector('input[name="eatting"]:checked')?.value,
		"division": document.querySelector('input[name="division"]:checked')?.value,
		"remarkNonNull": document.querySelector('input[name="remarkNonNull"]:checked')?.value,	
		"currentPageNo": currentPageNo
	}
	
	initContent(searchParams);
}

const getOffset = function getOffset(currentPageNo){
	return itemSizeOfPage*(currentPageNo-1);
}

initEvent();
initContent({
	"currentPageNo": getCurrentPageno(),
});