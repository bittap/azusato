/*
 * relate to play background music
 */
$(document).ready(function(){
	let modalTitle = "";
	let modalBody = "";
	let musucSrc = "";
	// resolve modal message
	if(window.language == "ko"){
		modalTitle = "배경음악재생";
		modalBody　= "배경음악을 재생 하시겠습니까?";
		musucSrc = "/music/background_ko.mp3";
	}else{
		modalTitle = "バックグラウンド音楽再生";
		modalBody　= "バックグラウンド音楽を再生しますか？";
		musucSrc = "/music/background_ja.mp3";
	}
	// initalize modal
	const twoBtnModal = modalCommon.modalTwoBtn(modalTitle,modalBody,function(){
		createAudioTag();
		console.log("yes start music play");
		document.querySelector('#background-audio').play();
	});

	// open confirm modal
	twoBtnModal.show();
	
	function createAudioTag(){
		const audioTag = document.createElement('audio');
		audioTag.setAttribute("id","background-audio");
		
		const sourceTag = document.createElement('source');
		sourceTag.setAttribute("src",musucSrc);
		sourceTag.setAttribute("type","audio/mpeg");
		
		audioTag.appendChild(sourceTag);
		
		document.body.appendChild(audioTag);
	}
})