const centerdClassName = "modal-dialog-centered";

const errorModal = new bootstrap.Modal(document.getElementById('errorBtnModal'), {
	  keyboard: false
});

function displayErrorModal(title, body){
	const modalTag = document.getElementById('errorBtnModal');
	modalTag.querySelector('.modal-title').textContent = title;
	modalTag.querySelector('.modal-body').textContent = body;
	errorModal.show();
	
}

/*
 * Initialize a modal with yes button and no button.
 * @param {string} title title of header part
 * @param {string} body message of body part
 * @param {function} yesBtnAction invoked action when the yes button is clicked. this is executed order that is closed modal and then "yesBtnAction". (can't not be 'null')
 * @param {function} noBtnAction invoked action when the no button is clicked. If this is null, close modal. If this is not null, execute it.
 */
function ModalTwoBtn(title, body , yesBtnAction, noBtnTitle, yesBtnTitle, noBtnAction, centered = false){
	// declare variables
	this.modalTag = document.getElementById('twoBtnModal');
	this.modalDialogTag = this.modalTag.querySelector('.modal-dialog');
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
	
	// set modal position to vertically center
	if(centered == true){
		this.modalDialogTag.classList.add(centerdClassName);
	}else{
		this.modalDialogTag.classList.remove(centerdClassName);
	}
	this.yesBtnTag.addEventListener('click',enclosingYesBtnAction(this.modalWithBootstrap));
	
	return this.modalWithBootstrap;
}

/*
 * Initialize a modal with first button and second button and third button.
 * @param {string} title title of header part
 * @param {string} body message of body part
 * @param {function} yesBtnAction invoked action when the yes button is clicked. this is executed order that is closed modal and then "yesBtnAction". (can't not be 'null')
 * @param {function} noBtnAction invoked action when the no button is clicked. If this is null, close modal. If this is not null, execute it.
 */
function ModalThreeBtn(title, body , firstBtnTitle,firstBtnAction, secondBtnTitle, secondBtnAction, thirdBtnTitle, thirdBtnAction,centered = false){
	const temp = document.querySelector('#template-threeBtnModal');
	const clone = temp.content.cloneNode(true);
	// declare variables
	this.modalTag = clone.querySelector('#threeBtnModal');
	this.modalDialogTag = this.modalTag.querySelector('.modal-dialog');
	this.firstBtnTag = this.modalTag.querySelector('#firstBtn');
	this.secondBtnTag = this.modalTag.querySelector('#secondBtn');
	this.thirdBtnTag = this.modalTag.querySelector('#thirdBtn');

	// create modal with bootstrap for using show, hide etc..
	// Note can't use display, fade etc ... without this
	this.modalWithBootstrap = new bootstrap.Modal(this.modalTag, {
		  keyboard: false
	});
	
	// replace title
	this.modalTag.querySelector('.modal-title').textContent = title;
	// replace body text
	this.modalTag.querySelector('.modal-body').textContent = body;
	
	// replace no button's text
	// if exist value replace text, else does not change 
	if(firstBtnTitle != null){
		this.firstBtnTag.textContent = firstBtnTitle;
	}
	
	// replcae yes button's text
	// if exist value replace text, else does not change 
	if(secondBtnTitle != null){
		this.secondBtnTag.textContent = secondBtnTitle;
	}
	
	if(thirdBtnTitle != null){
		this.thirdBtnTag.textContent = thirdBtnTitle;
	}
	
	thirdBtnAction == null ? 
			this.thirdBtnTag.addEventListener('click',this.modalWithBootstrap.hide()) : this.thirdBtnTag.addEventListener('click',thirdBtnAction);

	// for passing parameter in anonymous function
	const enclosingFirstBtnAction = (modalWithBootstrap) =>{
		return function(){
			firstBtnAction();
			modalWithBootstrap.hide();
		}
	}
	
	const enclosingSecondBtnAction = (modalWithBootstrap) =>{
		return function(){
			secondBtnAction();
			modalWithBootstrap.hide();
		}
	}
	
	// set modal position to vertically center
	if(centered == true){
		this.modalDialogTag.classList.add(centerdClassName);
	}else{
		this.modalDialogTag.classList.remove(centerdClassName);
	}
	this.firstBtnTag.addEventListener('click',enclosingFirstBtnAction(this.modalWithBootstrap));
	this.secondBtnTag.addEventListener('click',enclosingSecondBtnAction(this.modalWithBootstrap));
	
	document.body.appendChild(clone);
	
	return this.modalWithBootstrap;
}

