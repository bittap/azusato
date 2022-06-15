/*
 * お祝い作成、修正、リスト全体に使われる共通JS
 */
const getRandomImage = async function(){
	console.log("ランダムイメージ取得");
	const res = await fetch(apiUrl+"/profile/random",{
		method: 'GET',
		headers: apiCommon.header
	});
	
	const result = await res.json();
	
	if(!res.ok) {
		return Promise.reject(result);
	}else{
		console.log("結果",result);
		return result;
	}
}

/*
 * イメージのURLよりblobデータを取得する。
 */
const getBlobByImageUrl = async function(imageSrc){
	const res = await fetch(imageSrc,{
		method: 'GET',
		headers: apiCommon.header
	});
	
	const result = await res.blob();
	
	if(!res.ok) {
		return Promise.reject(result);
	}else{
		let resultWithFileName = {
				blob: result,
				filename: imageSrc.split("/").pop()
		}
		console.log("URLによるBlob取得結果",resultWithFileName);
		return resultWithFileName;
	}
}
