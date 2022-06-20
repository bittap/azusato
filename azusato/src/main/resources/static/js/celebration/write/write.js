const addnonMember = async function(){
	console.log("非会員ユーザ作成API");
	const res = await fetch(apiUrl+"/user/nonmember",{
		method: 'POST',
		headers: apiCommon.header,
		body: JSON.stringify({
			 name: document.querySelector('[name="name"]').value
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
	const res = await fetch(apiUrl+"/celebration",{
		method: 'POST',
		headers: apiCommon.header,
		body: JSON.stringify({
			 title: document.querySelector('[name="title"]').value, 
			 content: $('#summernote').summernote('code'),
			 name: document.querySelector('[name="name"]').value
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
			if(userInfo.profileImagePath == null){
				const randomImage = await getRandomImage();
				changeProfileByDefaultImage(randomImage.profileImagePath);
			}else{
				changeProfileByDefaultImage(userInfo.profileImagePath);
			}
		}else{
			const randomImage = await getRandomImage();
			changeProfileByDefaultImage(randomImage.profileImagePath);
		}
	}catch(e){
		console.log(e)
		modalCommon.displayErrorModal(e.title,e.message);
	}
}


/*
 * 作成ボタン押下時。
 * 1.ログインしていない状態だったら、非会員を作成
 * 2.プロフィールイメージ更新
 * 3.お祝い作成
 */
writeBtnTag.addEventListener('click', function(){
	modalCommon.displayTwoBtnModal(writeModalTitle,writeModalBody,async function(){
		try{
			// Loading画面を表示
			modalCommon.displayLoadingModal();
			// ログインしていない。
			if(Boolean(AUTHENTICATIONED) == false){
				await addnonMember();
			}
			// プロフィールイメージ更新
			await profileUpload();
			
			await celebrationAction();
			
			location.href = `/${language}/celebration`; 
		}catch(e){
			console.log(e);
			
			await asyncCommon.delay(LOADING_MODAL_DELAY);
			modalCommon.hideLoadingModal();
			
			modalCommon.displayErrorModal(e.title,e.message);
		}
	});
})

initialize();




