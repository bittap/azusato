/*
 * お祝い修正
 */


/*
 * Loading画面を表示し、修正APIを行う。
 */
const modifyCelebration = async function(){
	console.log("お祝い修正API");
	const res = await fetch(apiUrl+"/celebration"+ "/" +CELEBATION_NO,{
		method: 'PUT',
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

const getCelebation = async function(){
	console.log("お祝い情報取得");
	const res = await fetch(apiUrl + "/celebration" + "/" +CELEBATION_NO,{
		method: 'GET',
		headers: apiCommon.header
	});
	
	const result = await res.json();

	if(!res.ok) {
		return Promise.reject(result);
	}else{
		console.log("お祝い情報",result);
		return Promise.resolve(result);
	}
}

const initialize = async function(){
	console.log("初期画面設定");
	modalCommon.displayLoadingModal();
	
	try{
		const celebation = await getCelebation();
		document.querySelector("[name='name']").value = celebation.name;
		document.querySelector("[name='title']").value = celebation.title;
		// イメージ変更
		changeProfileByDefaultImage(celebation.profileImagePath);
		
		const content = await getContent(celebation.content);
		// 
		$('#summernote').summernote('code', content);
	}catch(e){
		console.log(e);
		// 以前ページに移動
		modalCommon.displayErrorModal(e.title,e.message,function(){
			history.back();
		});
	}finally{
		await asyncCommon.delay(LOADING_MODAL_DELAY);
		modalCommon.hideLoadingModal();
	}
}



writeBtnTag.addEventListener('click', function(){
	modalCommon.displayTwoBtnModal(modifyModalTitle,modifyModalBody,async function(){
		try{
			// Loading画面を表示
			modalCommon.displayLoadingModal();
			
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




