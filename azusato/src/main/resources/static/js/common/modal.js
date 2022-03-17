function ModalTwoBtn(title, body , yesBtnAction, noBtnTitle, yesBtnTitle, noBtnAction){
	this.modalTag = document.getElementById('twoBtnModal');
	this.noBtnTag = document.getElementById("twoBtnModal-no");
	this.yesBtnTag = document.getElementById("twoBtnModal-yes");
	// create modal with bootstrap for using show, hide etc..
	// Note can't use display, fade etc ... without this
	this.modalWithBootstrap = new bootstrap.Modal(document.getElementById('twoBtnModal'), {
		  keyboard: false
	});
	
	// replace title
	this.modalTag.querySelector('.modal-title').textContent = title;
	// replace body text
	this.modalTag.querySelector('.modal-body').textContent = body;
	
	// replace no button's text
	// if exist value replace text, else does not change 
	if(noBtnTitle != null){
		this.noBtnTag.textContent = noBtnTitle;
	}
	
	// replcae yes button's text
	// if exist value replace text, else does not change 
	if(yesBtnTitle != null){
		this.yesBtnTag.textContent = yesBtnTitle;
	}
	
	noBtnAction == null ? 
			this.noBtnTag.addEventListener('click',this.modalWithBootstrap.hide()) : this.noBtnTag.addEventListener('click',noBtnAction);

	// for passing parameter in anonymous function
	const enclosingYesBtnAction = (modalWithBootstrap) =>{
		return function(){
			yesBtnAction();
			modalWithBootstrap.hide();
		}
	}
	this.yesBtnTag.addEventListener('click',enclosingYesBtnAction(this.modalWithBootstrap));
}

