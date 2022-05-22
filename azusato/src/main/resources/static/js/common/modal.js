ModalCommon = function(){
	this.centerdClassName = "modal-dialog-centered";
	this.errorModal = new bootstrap.Modal(document.getElementById('errorBtnModal'), {
		  keyboard: false
	});
	this.confirmModal = new bootstrap.Modal(document.getElementById('confirmBtnModal'), {
		  keyboard: false
	});
	this.loadingEle = document.getElementById('loadingModal');
	this.loadingModal = new bootstrap.Modal(this.loadingEle, {
		  keyboard: false,
		  backdrop: "static"
	});
}

ModalCommon.prototype.displayLoadingModal = function(){
	this.loadingModal.show();
}
/*
 * 注意:show()とhide()は非同期のため　show()の次にすぐhide()を呼ぶとモーダルが表示されないままhide()が実行される。
 */
ModalCommon.prototype.hideLoadingModal = function(){
	this.loadingModal.hide();
}

ModalCommon.prototype.displayConfirmModal = function(title, body){
	this.displayOneBtnModal(title, body,document.getElementById('confirmBtnModal'),this.confirmModal);
}

ModalCommon.prototype.displayErrorModal = function(title, body){
	this.displayOneBtnModal(title, body,document.getElementById('errorBtnModal'),this.errorModal);
}
/*
 * APIエラーメッセージを表示。もし、title,messageがない場合は、基本メッセージのエラーを表示する。
 * @param {object} error title, messageに構成
 */
ModalCommon.prototype.displayApiErrorModal = function(error){
	let title;
	let body;
	
	if(error.title == null || error.body == null){
		title = DEFAULT_ERROR_TITLE;
		body = DEFAULT_ERROR_BODY;
	}else{
		title = error.title;
		body = error.body;
	}

	this.displayOneBtnModal(title, body,document.getElementById('errorBtnModal'),this.errorModal);
}

ModalCommon.prototype.displayOneBtnModal = function(title, body, targetTag, targetObj){
	targetTag.querySelector('.modal-title').textContent = title;
	targetTag.querySelector('.modal-body').innerHTML = this.convertNToBr(body);
	targetObj.show();
}

/*
 * \nタグを<br/>タグに置き換える。
 */
ModalCommon.prototype.convertNToBr = function(str){
	return str.replaceAll('\n','<br/>');
}

/*
 * Initialize a modal with yes button and no button.
 * @param {string} title title of header part
 * @param {string} body message of body part
 * @param {function} yesBtnAction invoked action when the yes button is clicked. this is executed order that is closed modal and then "yesBtnAction". (can't not be 'null')
 * @param {function} noBtnAction invoked action when the no button is clicked. If this is null, close modal. If this is not null, execute it.
 */
ModalCommon.prototype.modalTwoBtn = function(title, body , yesBtnAction, noBtnTitle, yesBtnTitle, noBtnAction, centered = false){
	const temp = document.querySelector('#template-twoBtnModal');
	const clone = temp.content.cloneNode(true);
	// declare variables
	this.modalTag = clone.querySelector('#twoBtnModal');
	this.modalDialogTag = this.modalTag.querySelector('.modal-dialog');
	this.noBtnTag = this.modalTag.querySelector('#twoBtnModal-no');
	this.yesBtnTag = this.modalTag.querySelector('#twoBtnModal-yes');
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
		this.modalDialogTag.classList.add(this.centerdClassName);
	}else{
		this.modalDialogTag.classList.remove(this.centerdClassName);
	}
	this.yesBtnTag.addEventListener('click',enclosingYesBtnAction(this.modalWithBootstrap));
	
	document.body.appendChild(clone);
	
	return this.modalWithBootstrap;
}

/*
 * Initialize a modal with first button and second button and third button.
 * @param {string} title title of header part
 * @param {string} body message of body part
 * @param {function} yesBtnAction invoked action when the yes button is clicked. this is executed order that is closed modal and then "yesBtnAction". (can't not be 'null')
 * @param {function} noBtnAction invoked action when the no button is clicked. If this is null, close modal. If this is not null, execute it.
 */
ModalCommon.prototype.modalThreeBtn = function(title, body , firstBtnTitle,firstBtnAction, secondBtnTitle, secondBtnAction, thirdBtnTitle, thirdBtnAction,centered = false){
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
		this.modalDialogTag.classList.add(this.centerdClassName);
	}else{
		this.modalDialogTag.classList.remove(this.centerdClassName);
	}
	this.firstBtnTag.addEventListener('click',enclosingFirstBtnAction(this.modalWithBootstrap));
	this.secondBtnTag.addEventListener('click',enclosingSecondBtnAction(this.modalWithBootstrap));
	
	document.body.appendChild(clone);
	
	return this.modalWithBootstrap;
}

