// initalize
const profileAvatarTag = document.querySelector("#profile-avatar");
const fileInputTag = document.querySelector("#file-input");
const FR = new FileReader();
const writeBtnTag = document.querySelector("#writeBtn");

// initalize modal
const profileModal = modalCommon.modalThreeBtn(profileModalTitle,profileModalBody,profileModalFirstBtnMsg,function(){
	fileInputTag.click();
},profileModalSecondBtnMsg,async function(){
	const res = await fetch(apiUrl+"/profile/randomProfile");
	const result = await res.json();
	changeProfileByTypeAndBase64(result.profileImageType,result.profileImageBase64);
});

// initalize modal
const writeBtnModal = modalCommon.modalTwoBtn(writeModalTitle,writeModalBody,function(){
	isSessionLoginInfo().then(async (result) => {
		// セッションがない。
		if(Boolean(result) == false){
			await addnonMember();
		}
		return await addCelebration();
	}).then(() => {
		location.href = `/${language}/celebration`; 
	}).catch(e =>{
		modalCommon.displayApiErrorModal(e);
	});
});

// initalize summbernote
$('#summernote').summernote({
    tabsize: 2,
    height: 500,
		lang: summernote_lang // default: 'en-US'
});

const addnonMember = async function(){
	console.log("非会員ユーザ作成API");
	const res = await fetch(apiUrl+"/user/add/nonmember",{
		method: 'POST',
		headers: {
		  'Accept': 'application/json',
		  'Content-Type': 'application/json'
		},
		 body: JSON.stringify({
			 name: document.querySelector('[name="name"]').value, 
			 profileImageType: document.querySelector('[name="profileImageType"]').value, 
			 profileImageBase64: document.querySelector('[name="profileImageBase64"]').value, 
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
 * Loding画面を表示し、投稿APIを行う。
 */
const addCelebration = async function(){
	console.log("お祝い投稿API");
	modalCommon.displayLoadingModal();
	const res = await fetch(apiUrl+"/celebration/add",{
		method: 'POST',
		headers: {
		  'Accept': 'application/json',
		  'Content-Type': 'application/json'
		},
		 body: JSON.stringify({
			 title: document.querySelector('[name="title"]').value, 
			 content: $('#summernote').summernote('code'),
			 name: document.querySelector('[name="name"]').value, 
			 profileImageType: document.querySelector('[name="profileImageType"]').value, 
			 profileImageBase64: document.querySelector('[name="profileImageBase64"]').value
		})
	});

	
	if(!res.ok) {
		await asyncCommon.delay(LOADING_MODAL_DELAY);
		modalCommon.hideLoadingModal();
		const result = await res.json();
		return Promise.reject(result);
	}else{
		return Promise.resolve();
	}
}

/*
 * ログインしているかどうかAPIを実行
 * ログインしている : true, していない : false
 * @return {boolean} true or false
 */
const isSessionLoginInfo = async function(){
	console.log("ログイン有無確認API");
	const res = await fetch(apiUrl+"/session/login-info");

	if(!res.ok) {
		const result = res.json();
		throw new Error(result);
	}else{
		return await res.json();
	}
}

const getUser = async function(){
	console.log("ユーザ情報取得");
	const res = await fetch(apiUrl+"/user");
	
	const result = await res.json();
	
	if(!res.ok) {
		return Promise.reject(result);
	}else{
		document.querySelector("[name='name']").value = result.name;
		changeProfileByTypeAndBase64(result.profileImageType,result.profileImageBase64);
	}
}

const getRandomImage = async function(){
	console.log("ランダムイメージ取得");
	const res = await fetch(apiUrl+"/profile/randomProfile");
	
	const result = await res.json();
	
	if(!res.ok) {
		return Promise.reject(result);
	}else{
		changeProfileByTypeAndBase64(result.profileImageType,result.profileImageBase64);
	}
}



const initialize = function(){
	isSessionLoginInfo().then(result =>{
		console.log(`ログイン有無 : ${result}`);
		// セッションがある。
		if(Boolean(result) == true){
			return getUser();
		// セッションがない。
		}else{
			return getRandomImage();
		}
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
	imageCommon.changeImageSrcBase64(profileAvatarTag,imageType,imageBase64);
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




