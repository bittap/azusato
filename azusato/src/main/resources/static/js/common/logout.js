/*
 * logoutAPIを実行する。
 */
const logout = async function(){
	await fetch(apiUrl+"/logout?",{
		method: 'POST',
		headers: apiCommon.header
	});
}