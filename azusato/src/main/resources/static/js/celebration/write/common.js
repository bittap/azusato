/*
* write.jsとmodify.jsの共通フィールド、メソッド
*/
// initalize
const profileAvatarTag = document.querySelector("#profile-avatar");
const fileInputTag = document.querySelector("#file-input");
const writeBtnTag = document.querySelector("#writeBtn");
// プロフィールイメージのアップロードタイプ
const UPLOAD_IMAGE_TYPE = {
	URL_IMAGE: 'URLイメージ',
	USER_UPLOAD_IMAGE: 'ユーザアップロードイメージ'
}
// 解決されたプロフィールイメージのアップロードタイプ
let resolvedUploadImageType;

//initalize summbernote
$('#summernote').summernote({
    tabsize: 2,
    height: 500,
		lang: summernote_lang // default: 'en-US'
});

/*
 * プロフィールをアップロードする。
 */
const profileUpload = async function(){
	const formData = new FormData();
	const FILE_FIELD_NAME = 'profileImage';
	if(resolvedUploadImageType == UPLOAD_IMAGE_TYPE.URL_IMAGE){
		let imageSrc = profileAvatarTag.getAttribute("src");
		const blobData = await getBlobByImageUrl(imageSrc);
		formData.append(FILE_FIELD_NAME,blobData.blob,blobData.filename);
	}else if(resolvedUploadImageType == UPLOAD_IMAGE_TYPE.USER_UPLOAD_IMAGE){
		formData.append(FILE_FIELD_NAME,fileInputTag.files[0]);
	}
	
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
 * バックエンドから取得した"CELEBATION_NO"があるかどうかによって作成・修正ページを判断する。
 * "CELEBATION_NO"がある : 修正ページ , ない　: 作成ページ
 */
const isWritePage = function(){
	return CELEBATION_NO == null ? Boolean(true) : Boolean(false);
}

/*
 * お祝い作成＆修正を行う。
 */
const celebrationAction = async function(){
	const formData = new FormData();
	
	formData.append("content",valueToText($('#summernote').summernote('code')));
	formData.append("title",document.querySelector('[name="title"]').value);
	formData.append("name",document.querySelector('[name="name"]').value);
	
	console.log("お祝いアクションAPI",formData);
	
	let methodType;
	let celebrationActionURL;
	if(isWritePage()){
		methodType = "POST";
		celebrationActionURL = apiUrl+"/celebration";
	}else{
		methodType = "PUT";
		celebrationActionURL = apiUrl+"/celebration"+ "/" +CELEBATION_NO;
	}
	
	const res = await fetch(celebrationActionURL,{
		method: methodType,
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

const valueToText = function(value){
	return new Blob([value], {
	    type: 'text/plain'
	});
}

/*
 * プロフィールイメージをURLにより更新する。
 */
const changeProfileByDefaultImage = function(imagePath){
	console.log("プロフィールイメージをURLにより更新");
	resolvedUploadImageType = UPLOAD_IMAGE_TYPE.URL_IMAGE;
	console.log("解決されたタイプ : "+resolvedUploadImageType);
	profileAvatarTag.setAttribute('src', imagePath);
}

/*
 * プロフィールイメージをユーザの入力したファイルにて更新する。
 */
const changeProfileByUserInputFile = function(loadedFile){
	console.log("プロフィールイメージをユーザの入力したファイルにて更新");
	resolvedUploadImageType = UPLOAD_IMAGE_TYPE.USER_UPLOAD_IMAGE;
	console.log("解決されたタイプ : "+resolvedUploadImageType);
	profileAvatarTag.setAttribute('src', loadedFile.target.result);
}

profileAvatarTag.addEventListener('click',() =>{
	modalCommon.displayThreeBtnModal(profileModalTitle,profileModalBody,profileModalFirstBtnMsg,profileModalSecondBtnMsg,
	// イメージアップロード選択
	function(){
		fileInputTag.click();
	// 基本イメージ選択
	},async function(){
		try{
			const result = await getRandomImage();
			changeProfileByDefaultImage(result.profileImagePath);
		}catch(e){
			console.log(e)
			modalCommon.displayErrorModal(e.title,e.message);
		}
	});
});

let fileType;

/*
 * イメージチェインジが行ったら、ファイルを読み込んでbase64形式で更新する。
 *
 */
fileInputTag.addEventListener('change',function(){
	if(!this.files || !this.files[0]) return;
	
	fileType = this.files[0].type;
	
	if(fileType !=  "image/jpeg" && fileType != "image/png"){
		modalCommon.displayErrorModal("I-0004",badRequestProfileType);
		return;
	}
	const FR = new FileReader();
	
	FR.addEventListener("load", function(loadedFile){
		console.log(loadedFile);
		changeProfileByUserInputFile(loadedFile);
	});
	
	FR.readAsDataURL(this.files[0]);
});