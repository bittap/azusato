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
		sourceTag.setAttribute("src",BACKGROUND_MUSIC_MODAL_SRC);
		sourceTag.setAttribute("type","audio/mpeg");
		
		audioTag.appendChild(sourceTag);
		
		document.body.appendChild(audioTag);
	}
})