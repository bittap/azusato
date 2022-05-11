// initalize
const profileAvatarTag = document.querySelector("#profile-avatar");
const fileInputTag = document.querySelector("#file-input");
// initalize modal
// TODO vertical https://pisuke-code.com/js-convert-string-to-boolean/
const profileModal =new ModalThreeBtn(profileModalTitle,profileModalBody,profileModalFirstBtnMsg,function(){
	fileInputTag.click();
},profileModalSecondBtnMsg,async function(){
	const res = await fetch(apiUrl+"/profile/randomProfile");
	const result = await res.json();
	changeProfileByTypeAndBase64(result.profileImageType,result.profileImageBase64);
});

// initalize summbernote
$('#summernote').summernote({
    tabsize: 2,
    height: 500,
		lang: summernote_lang // default: 'en-US'
});

function initialize(){
	fetch(apiUrl+"/session/login-info").then((res)=>{
		return res.json();
	}).then(result => {
		console.log(result);
		// セッションがある。
		if(result == "true"){
			return fetch(apiUrl+"/user")
		// セッションがない。
		}else{
			return fetch(apiUrl+"/profile/randomProfile")
		}
	}).then(res => {
		return res.json();
	}).then(result => {
		changeProfileByTypeAndBase64(result.profileImageType,result.profileImageBase64);
	}).catch(e =>{
		console.log(`error : ${e}`);
	});
}

/*
 * プロフィールイメージを更新する。
 */
function changeProfileByTypeAndBase64(imageType, imageBase64){
	profileAvatarTag.setAttribute("src",`data: ${imageType} ;base64,${imageBase64}`);
}

/*
 * プロフィールイメージを更新する。
 */
function changeProfileByBase64(base64){
	profileAvatarTag.setAttribute("src",base64);
}

profileAvatarTag.addEventListener('click',() =>{
	// open confirm modal
	profileModal.show();
});

fileInputTag.addEventListener('change',function(){
	if(!this.files || !this.files[0]) return;
	
	const fileType = this.files[0].type;
	
	if(fileType !=  "image/jpeg" && fileType != "image/png"){
		displayErrorModal("I-0004",badRequestProfileType);
		return;
	}
	
	const FR = new FileReader();
	
	FR.addEventListener("load", function(loadedFile){
		changeProfileByBase64(loadedFile.target.result);
	});
	
	FR.readAsDataURL(this.files[0]);
})




initialize();




