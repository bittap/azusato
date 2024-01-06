/*
 * プロポーズのyoutube関連JS
 */
// 2. This code loads the IFrame Player API code asynchronously.
const proposeScriptTag = document.createElement('script');

proposeScriptTag.src = "https://www.youtube.com/iframe_api";
const proposeFirstScriptTag = document.getElementsByTagName('script')[0];
proposeFirstScriptTag.parentNode.insertBefore(proposeScriptTag, proposeFirstScriptTag);


// 3. This function creates an <iframe> (and YouTube player)
//    after the API code downloads.
let proposePlayer;

function onYouTubeIframeAPIReady() {
	proposePlayer = new YT.Player('propose-youtube', {
		videoId: 'T1ZWp7GIqaQ',
		events: {
			'onStateChange': onPlayerStateChange
		}
	});
}

// 5. The API calls this function when the player's state changes.
//    The function indicates that when playing a video (state=1),
//    the player should play for six seconds and then stop.
let done = false;

function onPlayerStateChange(event) {
	if (event.data == YT.PlayerState.PLAYING && !done) {
		//バックグラウンドの音楽が存在する場合は、動画と一緒に再生されるため音楽は停止する
		if (typeof stopBackgroundMusic === 'function') {
			stopBackgroundMusic();
		}
		done = true;
	}
}