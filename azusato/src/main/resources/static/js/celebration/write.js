// initalize
const profileAvatar = document.querySelector("#profile-avatar");
// initalize modal
// TODO vertical https://pisuke-code.com/js-convert-string-to-boolean/
const local_twoBtnModal =new ModalTwoBtn("プロフィール写真変更","プロフィール写真変更を行いますか？",function(){
	alert("click!");
});

profileAvatar.addEventListener('click',() =>{
	// open confirm modal
	local_twoBtnModal.modalWithBootstrap.show();
});
