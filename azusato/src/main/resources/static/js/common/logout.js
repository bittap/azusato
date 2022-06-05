/*
 * logoutAPIを実行する。
 */
const logout = async function(){
	await fetch(apiUrl+"/logout?",{
		method: 'POST',
		headers: {
		  'Accept': 'application/json',
		  'Content-Type': 'application/json',
		  'Accept-Language': language,
		}
	});
}