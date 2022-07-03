/*
 * 動画再生関連JS。
 * 「/js/backgroundMusic.js」に依存する。
 */
$(document).ready(function(){
	
	/*
	 * 動画をクリックすると動画を再生し、バックエンド音楽の再生を止める。
	 */
	$('video').on('touchstart click', function(){
		console.log("video click event");
		if(this.paused){
			stopBackgroundMusic();
			this.play();
		}else{
			this.pause();
		}
	});
	
	/*
	 * 動画フレームの中にある再生ボタンを押下するとバックエンド音楽の再生を止める。
	 */
	$('video').on('play', function(){
		console.log("video play event");
		stopBackgroundMusic();
	});
})