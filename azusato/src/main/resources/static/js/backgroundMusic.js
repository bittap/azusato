const MUSIC_PATH = document.currentScript.getAttribute('music-path');
const MUSIC_TARGET = "#background-audio";

/*
* 音楽の再生を停止する
*/
function stopBackgroundMusic(){
	document.querySelector(MUSIC_TARGET).pause();
}
/*
 * relate to play background music
 */
$(document).ready(function(){
	// モーダルを表示
	modalCommon.displayTwoBtnModal(BACKGROUND_MUSIC_MODAL_TITLE,BACKGROUND_MUSIC_MODAL_BODY,function(){
		console.log("yes start music play");
		document.querySelector(MUSIC_TARGET).play();
	});

	function createAudioTag(){
		const audioTag = document.createElement('audio');
		audioTag.setAttribute("id","background-audio");
		
		const sourceTag = document.createElement('source');
		sourceTag.setAttribute("src",MUSIC_PATH + BACKGROUND_MUSIC_MODAL_FILENAME);
		sourceTag.setAttribute("type","audio/mpeg");
		
		audioTag.appendChild(sourceTag);
		
		document.body.appendChild(audioTag);
	}
	
	createAudioTag();
})