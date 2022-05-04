// resolve modal message
if(window.language == "ko"){
	profileModalTitle = "배경음악재생";
	profileModalBody　= "프로필사진 변경을 하시겠습니까?";
	summernote_lang = "ko-KR";
}else{
	profileModalTitle = "プロフィール写真変更";
	profileModalBody　= "プロフィール写真変更を行いますか？";
	summernote_lang = "ja-JP";
}
// initalize
const profileAvatar = document.querySelector("#profile-avatar");
// initalize modal
// TODO vertical https://pisuke-code.com/js-convert-string-to-boolean/
const local_twoBtnModal =new ModalTwoBtn(profileModalTitle,profileModalBody,function(){
	alert("click!");
});

// initalize summbernote
$('#summernote').summernote({
    tabsize: 2,
    height: 500,
		lang: summernote_lang // default: 'en-US'
});

profileAvatar.addEventListener('click',() =>{
	// open confirm modal
	local_twoBtnModal.show();
});


