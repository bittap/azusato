
if(document.querySelector('#logout_a') != null){
	document.querySelector('#logout_a').addEventListener('click',function(){
		modalCommon.displayTwoBtnModal(LOGOUT_MODAL_TITLE,LOGOUT_MODAL_BODY,async function(){
			
			try{
				await logout();
			}catch(e){
				console.log(e);
				modalCommon.displayErrorModal(e.title,e.message);
			}
		});
});
}


/*
 * logoutAPIを実行する。
 */
const logout = async function(){
	const res = await fetch(apiUrl+"/logout?",{
		method: 'POST',
		headers: apiCommon.header
	});
	
	if(!res.ok) {
		return Promise.reject(result);
	}else{
		location.href = `/${language}`; 
	}
}