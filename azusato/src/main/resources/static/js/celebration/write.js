// initalize
const profileAvatarTag = document.querySelector("#profile-avatar");
const fileInputTag = document.querySelector("#file-input");
const FR = new FileReader();
const writeBtnTag = document.querySelector("#writeBtn");

// initalize modal
const profileModal =new ModalThreeBtn(profileModalTitle,profileModalBody,profileModalFirstBtnMsg,function(){
	fileInputTag.click();
},profileModalSecondBtnMsg,async function(){
	const res = await fetch(apiUrl+"/profile/randomProfile");
	const result = await res.json();
	changeProfileByTypeAndBase64(result.profileImageType,result.profileImageBase64);
});

// initalize modal
const writeBtnModal =new ModalTwoBtn(writeModalTitle,writeModalBody,function(){
	isSessionLoginInfo().then(result =>{
		// セッションがある。
		if(result == "true"){
			// TODO
			return fetch(apiUrl+"/user")
		// セッションがない。
		}else{
			return addnonMember();
		}
	}).then(result => {
		return addCelebration();
	}).then(result => {
		modalCommon.displayErrorModal("完了title","完了body");
	})
});

// initalize summbernote
$('#summernote').summernote({
    tabsize: 2,
    height: 500,
		lang: summernote_lang // default: 'en-US'
});

const addnonMember = async function(){
	const res = await fetch(apiUrl+"/user/add/nonmember",{
		method: 'POST',
		headers: {
		  'Accept': 'application/json',
		  'Content-Type': 'application/json'
		},
		 body: JSON.stringify({
			 name: document.querySelector('[name="name"]').value, 
			 profileImageType: document.querySelector('[name="profileImageType"]').value, 
			 profileImageBase64: document.querySelector('[name="profileImageType"]').value, 
		})
	});
	return await res.json();
}

const addCelebration = async function(){
	const res = await fetch(apiUrl+"/celebration/add",{
		method: 'POST',
		headers: {
		  'Accept': 'application/json',
		  'Content-Type': 'application/json'
		},
		 body: JSON.stringify({
			 title: document.querySelector('[name="title"]').value, 
			 content: $('#summernote').summernote('code')
		})
	});
	return await res.json();
}

/*
 * ログインしているかどうかAPIを実行
 * ログインしている : true, していない : false
 * @return {boolean} true or false
 */
const isSessionLoginInfo = async function(){
	const res = await fetch(apiUrl+"/session/login-info");
	// TODO検証
	// もしこれでできたら、他のAPIも修正
	if(!res.ok) {
		const result = res.json();
		throw new Error(result);
	}else{
		return await res.json();
	}
}

const initialize = function(){
	isSessionLoginInfo().then(result =>{
		console.log(`ログイン有無 : ${result}`);
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
		modalCommon.displayApiErrorModal(e);
	});
}

/*
 * プロフィールイメージを更新する。
 */
const changeProfileByTypeAndBase64 = function(imageType, imageBase64){
	document.querySelector("[name='profileImageType']").value = imageType;
	document.querySelector("[name='profileImageBase64']").value = imageBase64;
	profileAvatarTag.setAttribute("src",`data: ${imageType} ;base64,${imageBase64}`);
}

profileAvatarTag.addEventListener('click',() =>{
	// open confirm modal
	profileModal.show();
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

writeBtnTag.addEventListener('click', function(){
	writeBtnModal.show();
})




initialize();




