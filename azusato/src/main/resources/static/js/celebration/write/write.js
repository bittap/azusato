
const addnonMember = async function(){
	console.log("非会員ユーザ作成API");
	const res = await fetch(apiUrl+"/user/nonmember",{
		method: 'POST',
		headers: apiCommon.header,
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
 * Loading画面を表示し、投稿APIを行う。
 */
const addCelebration = async function(){
	console.log("お祝い投稿API");
	modalCommon.displayLoadingModal();
	const res = await fetch(apiUrl+"/celebration",{
		method: 'POST',
		headers: apiCommon.header,
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


const initialize = async function(){
	console.log("初期画面設定");
	try{
		console.log(`ログイン有無 : ${AUTHENTICATIONED}`);
		
		// セッションがある場合
		if(Boolean(AUTHENTICATIONED) == true){
			const userInfo = await getUser();
			document.querySelector("[name='name']").value = userInfo.name;
			if(userInfo.profileImageType == null || userInfo.profileImageBase64 == null){
				const randomImage = await getRandomImage();
				changeProfileByTypeAndBase64(randomImage.profileImageType,randomImage.profileImageBase64);
			}else{
				changeProfileByTypeAndBase64(userInfo.profileImageType,userInfo.profileImageBase64);
			}
		}else{
			const randomImage = await getRandomImage();
			changeProfileByTypeAndBase64(randomImage.profileImageType,randomImage.profileImageBase64);
		}
	}catch(e){
		console.log(e)
		modalCommon.displayErrorModal(e.title,e.message);
	}
}



writeBtnTag.addEventListener('click', function(){
	modalCommon.displayTwoBtnModal(writeModalTitle,writeModalBody,async function(){
		try{
			// ログインしていない。
			if(Boolean(AUTHENTICATIONED) == false){
				await addnonMember();
			}
			await addCelebration();
			
			location.href = `/${language}/celebration`; 
		}catch(e){
			console.log(e)
			modalCommon.displayErrorModal(e.title,e.message);
		}
	});
})

initialize();




