/*
 * お祝い修正
 */


/*
 * Loading画面を表示し、修正APIを行う。
 */
const modifyCelebration = async function(){
	console.log("お祝い修正API");
	modalCommon.displayLoadingModal();
	const res = await fetch(apiUrl+"/celebration"+ "/" +CELEBATION_NO,{
		method: 'PUT',
		headers: {
		  'Accept': 'application/json',
		  'Content-Type': 'application/json',
		  'Accept-Language': language  
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

const getCelebation = async function(){
	console.log("お祝い情報取得");
	modalCommon.displayLoadingModal();
	const res = await fetch(apiUrl + "/celebration" + "/" +CELEBATION_NO,{
		method: 'GET',
		headers: {
		  'Accept': 'application/json',
		  'Content-Type': 'application/json',
		  'Accept-Language': language
		}
	});
	
	const result = await res.json();
	
	await asyncCommon.delay(LOADING_MODAL_DELAY);
	modalCommon.hideLoadingModal();
	
	if(!res.ok) {
		return Promise.reject(result);
	}else{
		console.log("お祝い情報",result);
		return Promise.resolve(result);
	}
}

const initialize = async function(){
	console.log("初期画面設定");
	try{
		const celebation = await getCelebation();
		document.querySelector("[name='name']").value = celebation.name;
		document.querySelector("[name='title']").value = celebation.title;
		// イメージ変更
		changeProfileByTypeAndBase64(celebation.profileImageType,celebation.profileImageBase64);
		// 
		$('#summernote').summernote('code', celebation.content);
	}catch(e){
		console.log(e);
		// 以前ページに移動
		modalCommon.displayErrorModal(e.title,e.message,function(){
			history.back();
		});
	}
}



writeBtnTag.addEventListener('click', function(){
	modalCommon.displayTwoBtnModal(modifyModalTitle,modifyModalBody,async function(){
		try{
			await modifyCelebration();
			
			location.href = `/${language}/celebration`; 
		}catch(e){
			console.log(e)
			modalCommon.displayErrorModal(e.title,e.message);
		}
	});
})

initialize();




