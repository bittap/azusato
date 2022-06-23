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

/*
 * 画面を初期化する。
 * クッキー「ユーザID保存」に値がある場合は、そのユーザIDをINPUTに入れておき、チェックボックスをチェックする。
 */
const initalize = function(){
	const SAVED_ID = cookieCommon.getCookie(COOKIE_LOGIN_SAVE_ID_NAME);
	
	if(SAVED_ID != ""){
		document.querySelector('[name="id"]').value = SAVED_ID;
		document.querySelector('[name="savedId"]').checked = true;
	}
}

initalize();




