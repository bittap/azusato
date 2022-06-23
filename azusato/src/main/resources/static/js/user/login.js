// URLSearchParamsを取得
const urlParams = urlCommon.urlParams;

/*
 * loginAPIを実行する。
 */
const login = async function(){
	const res = await fetch(apiUrl+"/login?"+ new URLSearchParams({
			"id": document.querySelector('[name="id"]').value,
			"password": document.querySelector('[name="password"]').value,
			"savedId": document.querySelector('[name="savedId"]').checked
		}),{
		method: 'POST',
		headers: apiCommon.header
	});

	if(!res.ok) {
		const result = await res.json();
		return Promise.reject(result);
	}else{
		return Promise.resolve();
	}
}

/*
 * ログインボタンクリック成功するとメイン画面に移動させる。
 */
document.querySelector('#login_btn').addEventListener('click',async function(){
	try{
		await login();
		location.href = `/${language}`; 
	}catch(e){
		console.log(e)
		modalCommon.displayErrorModal(e.title,e.message);
	}
})

/*
 *  Enterキーを押下すると、ログインボタンのクリックEventを発火する。
 */
$(document).keypress(function (e) {
	// enter key
    if (e.which == 13) {
    	document.querySelector('#login_btn').click();
    }
});




