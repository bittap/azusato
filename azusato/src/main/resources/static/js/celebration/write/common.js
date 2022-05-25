/*
* write.jsとmodify.jsの共通フィールド、メソッド
*/
// initalize
const profileAvatarTag = document.querySelector("#profile-avatar");
const fileInputTag = document.querySelector("#file-input");
const FR = new FileReader();
const writeBtnTag = document.querySelector("#writeBtn");

//initalize summbernote
$('#summernote').summernote({
    tabsize: 2,
    height: 500,
		lang: summernote_lang // default: 'en-US'
});

const getRandomImage = async function(){
	console.log("ランダムイメージ取得");
	const res = await fetch(apiUrl+"/profile/random");
	
	const result = await res.json();
	
	if(!res.ok) {
		return Promise.reject(result);
	}else{
		return result;
	}
}

/*
 * プロフィールイメージを更新する。
 */
const changeProfileByTypeAndBase64 = function(imageType, imageBase64){
	document.querySelector("[name='profileImageType']").value = imageType;
	document.querySelector("[name='profileImageBase64']").value = imageBase64;
	imageCommon.changeImageSrcBase64(profileAvatarTag,imageType,imageBase64);
}

profileAvatarTag.addEventListener('click',() =>{
	modalCommon.displayThreeBtnModal(profileModalTitle,profileModalBody,profileModalFirstBtnMsg,profileModalSecondBtnMsg,
	function(){
		fileInputTag.click();
	},async function(){
		try{
			const result = await getRandomImage();
			changeProfileByTypeAndBase64(result.profileImageType,result.profileImageBase64);
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
		displayErrorModal("I-0004",badRequestProfileType);
		return;
	}
	
	FR.addEventListener("load", function(loadedFile){
		console.log(loadedFile);
		const fullBase64 = loadedFile.target.result;
		// 削除 data:*/*;base64,
		const base64 = fullBase64.replace("data:","").replace(/^.+,/,"");
		changeProfileByTypeAndBase64(fileType,base64);
	});
	
	FR.readAsDataURL(this.files[0]);
});