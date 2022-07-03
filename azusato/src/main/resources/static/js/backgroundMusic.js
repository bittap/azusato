const MUSIC_PATH = document.currentScript.getAttribute('music-path');

/*
 * relate to play background music
 */
$(document).ready(function(){
	// モーダルを表示
	modalCommon.displayTwoBtnModal(BACKGROUND_MUSIC_MODAL_TITLE,BACKGROUND_MUSIC_MODAL_BODY,function(){
		createAudioTag();
		console.log("yes start music play");
		document.querySelector('#background-audio').play();
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
})

/*
 * バックグラウンド音楽の再生を止める。
 * 「#background-audio」がある場合それをstopする。
 */
function stopBackgroundMusic(){
	const audio = document.querySelector("#background-audio");
	// exist a element
	if(audio != null){
		audio.pause();
	}
	
}