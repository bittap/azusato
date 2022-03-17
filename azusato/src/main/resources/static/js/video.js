$(document).ready(function(){
	
	$('video').click(function(){
		console.log("video click event");
		if(this.paused){
			stopBackgroundMusic();
			$(this).play();
		}else{
			$(this).pause();
		}
	});
	
	function stopBackgroundMusic(){
		const audio = document.querySelector("#background-audio");
		// exist a element
		if(audio != null){
			audio.pause();
		}
		
	}
})