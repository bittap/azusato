
// initalize modal
const writeBtnModal = modalCommon.modalTwoBtn(modifyModalTitle,modifyModalBody,async function(){
	try{
		const siginIn = await isSessionLoginInfo();
		// セッションがない。
		if(Boolean(siginIn) == false){
			await addnonMember();
		}
		await addCelebration();
		
		location.href = `/${language}/celebration`; 
	}catch(e){
		console.log(e)
		modalCommon.displayApiErrorModal(e);
	}
});

/*
 * Loading画面を表示し、投稿APIを行う。
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

const initialize = async function(){
	console.log("初期画面設定");
	try{
		const siginIn = await isSessionLoginInfo();
		console.log(`ログイン有無 : ${siginIn}`);
		
		// セッションがある場合
		if(Boolean(siginIn) == true){
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
		modalCommon.displayApiErrorModal(e);
	}
}



writeBtnTag.addEventListener('click', function(){
	writeBtnModal.show();
})

initialize();




