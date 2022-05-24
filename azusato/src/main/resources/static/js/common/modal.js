/*
 * モーダルを初期化する。
 * 表示・非表示必要なbootstrapのobjectを宣言しておく。
 */
ModalCommon = function(){
	this.centerdClassName = "modal-dialog-centered";
	// element宣言
	this.errorModalEle = document.getElementById('errorBtnModal');
	this.loadingModalEle = document.getElementById('loadingModal');
	this.oneBtnModalEle = document.getElementById('oneBtnModal');
	this.twoBtnModalEle = document.getElementById('twoBtnModal');
	this.threeBtnModalEle = document.getElementById('threeBtnModal');
	
	// object宣言
	this.errorModal = new bootstrap.Modal(this.errorModalEle, {
		  keyboard: false
	});
	this.loadingModal = new bootstrap.Modal(this.loadingModalEle, {
		  keyboard: false,
		  backdrop: "static"
	});
	this.oneBtnModal = new bootstrap.Modal(this.oneBtnModalEle, {
		  keyboard: false
	});
	this.twoBtnModal = new bootstrap.Modal(this.twoBtnModalEle, {
		  keyboard: false
	});
	this.threeBtnModal = new bootstrap.Modal(this.threeBtnModalEle, {
		  keyboard: false
	});
}

/*
 * ローディング画面を表示する。
 */
ModalCommon.prototype.displayLoadingModal = function(){
	this.loadingModal.show();
}
/*
 * ローディング画面を非表示にする。
 * 注意:show()とhide()は非同期のため　show()の次にすぐhide()を呼ぶとモーダルが表示されないままhide()が実行される。
 */
ModalCommon.prototype.hideLoadingModal = function(){
	this.loadingModal.hide();
}

/*
 * \nタグを<br/>タグに置き換える。
 */
ModalCommon.prototype.convertNToBr = function(str){
	return str.replaceAll('\n','<br/>');
}

/*
 * エラーモーダルを表示する。titleまたはbodyがnullの場合は基本メッセージを表示する。
 * displayModalに伝播する。
 * @param {string} {nullAble} [title=DEFAULT_ERROR_TITLE] モーダルのタイトル。
 * @param {string} {nullAble} [body=DEFAULT_ERROR_BODY] モーダルの本文
 * @param {function} {nullAble} [okBtnAction=モーダルクローズ]
 * @param {string} {nullAble} [okBtnTitle=MODAL_YES_TEXT]
 * @param {boolean} {nullAble} [okBtnAutoClosed=true]
 * @param {boolean} {nullAble} [displayCloseBtn=true]
 * @param {boolean} {nullAble} [centered=false]
 */
ModalCommon.prototype.displayErrorModal = function(title, body, okBtnAction, okBtnTitle, okBtnAutoClosed ,displayCloseBtn , centered){
	const BTN_COUNT = 1;
	okBtnTitle = okBtnTitle == null ? MODAL_YES_TEXT :  okBtnTitle;
	okBtnAutoClosed = okBtnAutoClosed == null ? true :  okBtnAutoClosed;
	displayCloseBtn = displayCloseBtn == null ? true :  displayCloseBtn;
	centered = centered == null ? false :  centered;
	
	if(title == null || body == null){
		title = DEFAULT_ERROR_TITLE;
		body = DEFAULT_ERROR_BODY;
	}else{
		title = error.title;
		body = error.message;
	}
	
	this.displayModal(title,body,BTN_COUNT,this.errorModalEle,this.errorModal,
			okBtnAction,okBtnAutoClosed,null,null,
			okBtnTitle,null,displayCloseBtn,centered,
			null,null,null);
}

/*
 * ボタンが一つのモーダルを表示する。
 * displayModalに伝播する。
 * @param {string} {notNull} [title] モーダルのタイトル。
 * @param {string} {notNull} [body] モーダルの本文
 * @param {function} {nullAble} [okBtnAction=モーダルクローズ]
 * @param {string} {nullAble} [okBtnTitle=MODAL_YES_TEXT]
 * @param {boolean} {nullAble} [okBtnAutoClosed=true]
 * @param {boolean} {nullAble} [displayCloseBtn=true]
 * @param {boolean} {nullAble} [centered=false]
 */
ModalCommon.prototype.displayOneBtnModal = function(title, body, okBtnAction, okBtnTitle, okBtnAutoClosed ,displayCloseBtn , centered){
	const BTN_COUNT = 1;
	okBtnTitle = okBtnTitle == null ? MODAL_YES_TEXT :  okBtnTitle;
	okBtnAutoClosed = okBtnAutoClosed == null ? true :  okBtnAutoClosed;
	displayCloseBtn = displayCloseBtn == null ? true :  displayCloseBtn;
	centered = centered == null ? false :  centered;
	
	this.displayModal(title,body,BTN_COUNT,this.oneBtnModalEle,this.oneBtnModal,
			okBtnAction,okBtnAutoClosed,null,null,
			okBtnTitle,null,displayCloseBtn,centered,
			null,null,null);
}

/*
 * ボタンが二つのモーダルを表示する。
 * displayModalに伝播する。
 * @param {string} {notNull} [title] モーダルのタイトル。
 * @param {string} {notNull} [body] モーダルの本文
 * @param {function} {nullAble} [okBtnAction=モーダルクローズ]
 * @param {function} {nullAble} [noBtnAction=モーダルクローズ]
 * @param {string} {nullAble} [okBtnTitle=MODAL_YES_TEXT]
 * @param {string} {nullAble} [noBtnTitle=MODAL_NO_TEXT]
 * @param {boolean} {nullAble} [okBtnAutoClosed=true]
 * @param {boolean} {nullAble} [noBtnAutoClosed=true]
 * @param {boolean} {nullAble} [displayCloseBtn=true]
 * @param {boolean} {nullAble} [centered=false]
 */
ModalCommon.prototype.displayTwoBtnModal = function(title, body, okBtnAction, noBtnAction, okBtnTitle, noBtnTitle, okBtnAutoClosed , noBtnAutoClosed, displayCloseBtn , centered){
	const BTN_COUNT = 2;
	okBtnTitle = okBtnTitle == null ? MODAL_YES_TEXT :  okBtnTitle;
	noBtnTitle = noBtnTitle == null ? MODAL_NO_TEXT :  noBtnTitle;
	okBtnAutoClosed = okBtnAutoClosed == null ? true :  okBtnAutoClosed;
	noBtnAutoClosed = noBtnAutoClosed == null ? true :  noBtnAutoClosed;
	displayCloseBtn = displayCloseBtn == null ? true :  displayCloseBtn;
	centered = centered == null ? false :  centered;
	
	this.displayModal(title,body,BTN_COUNT,this.twoBtnModalEle,this.twoBtnModal,
			okBtnAction,okBtnAutoClosed,noBtnAction,noBtnAutoClosed,
			okBtnTitle,noBtnTitle,displayCloseBtn,centered,
			null,null,null);
}

/*
 * ボタンが三つのモーダルを表示する。
 * displayModalに伝播する。
 * @param {string} {notNull} [title] モーダルのタイトル。
 * @param {string} {notNull} [body] モーダルの本文
 * @param {string} {notNull} firstBtnTitle 一番左のボタンのテキスト。
 * @param {string} {notNull} secondBtnTitle 一番左の二番目のボタンのテキスト。
 * @param {function} {nullAble} [firstBtnAction=モーダルクローズ]
 * @param {function} {nullAble} [secondBtnAction=モーダルクローズ]
 * @param {boolean} {nullAble} [firstBtnAutoClosed=true]
 * @param {boolean} {nullAble} [secondBtnAutoClosed=true]
 * @param {boolean} {nullAble} [displayCloseBtn=true]
 * @param {boolean} {nullAble} [centered=false]
 * @param {function} {nullAble} [thridBtnAction=モーダルクローズ]
 * @param {boolean} {nullAble} [thirdBtnAutoClosed=true]
 * @param {string} {nullAble} [thridBtnTitle=MODAL_NO_TEXT]
 */
ModalCommon.prototype.displayThreeBtnModal = function(title , body,  firstBtnTitle, secondBtnTitle,firstBtnAction,secondBtnAction,
		firstBtnAutoClosed, secondBtnAutoClosed, displayCloseBtn , centered, thridBtnAction, thirdBtnAutoClosed,thridBtnTitle){
	
	const BTN_COUNT = 3;
	thridBtnTitle = thridBtnTitle == null ? MODAL_NO_TEXT :  thridBtnTitle;
	firstBtnAutoClosed = firstBtnAutoClosed == null ? true :  firstBtnAutoClosed;
	secondBtnAutoClosed = secondBtnAutoClosed == null ? true :  secondBtnAutoClosed;
	thirdBtnAutoClosed = thirdBtnAutoClosed == null ? true :  thirdBtnAutoClosed;
	displayCloseBtn = displayCloseBtn == null ? true :  displayCloseBtn;
	centered = centered == null ? false :  centered;
	
	this.displayModal(title,body,BTN_COUNT,this.threeBtnModalEle,this.threeBtnModal,
			firstBtnAction,firstBtnAutoClosed,secondBtnAction,secondBtnAutoClosed,
			firstBtnTitle,secondBtnTitle,displayCloseBtn,centered,
			thridBtnAction,thirdBtnAutoClosed,thridBtnTitle);
}


/*
 * モーダルを表示する。
 * @param {string} title モーダルのタイトル
 * @param {string} body モーダルの本文
 * @param {int} btnCount ボタンの数
 * @param {document} targetEle 対象のモーダルElement
 * @param {bootstrap} targetObj 表示・非表示を行う。
 * @param {function} firstBtnAction 一番左のボタンをクリックした時の挙動。nullの場合はクローズだけを行う。
 * @param {boolean} firstBtnAutoClosed true : firstBtnAction実行後自動クローズ。 false : firstBtnAction実行後自動クローズを行わないモーダルを閉じたい場合は手動でfirstBtnActionに宣言する必要がある。
 * @param {function} secondBtnAction 一番左の二番目のボタンをクリックした時の挙動。nullの場合はクローズだけを行う。
 * @param {boolean} secondBtnAutoClosed true : twoBtnAction実行後自動クローズ。 false : twoBtnAction実行後自動クローズを行わないモーダルを閉じたい場合は手動でfirstBtnActionに宣言する必要がある。
 * @param {string} firstBtnTitle 一番左のボタンのテキスト。
 * @param {string} secondBtnTitle 一番左の二番目のボタンのテキスト。
 * @param {boolean} displayCloseBtn true : クローズボタン表示。 false : クローズボタン非表示
 * @param {boolean} centered true : モーダルをセンターに表示。 false : モーダルをセンターに表示しない。
 * @param {function} thridBtnAction 一番左の三番目のボタンをクリックした時の挙動。nullの場合はクローズだけを行う。
 * @param {function} thirdBtnAutoClosed 一番左の三番目のボタンをクリックした時の挙動。nullの場合はクローズだけを行う。
 * @param {string} thridBtnTitle 一番左の三番目のボタンのテキスト。
 */
ModalCommon.prototype.displayModal = function(title,body,btnCount, targetEle , targetObj , 
		firstBtnAction, firstBtnAutoClosed, secondBtnAction, secondBtnAutoClosed, 
		firstBtnTitle, secondBtnTitle, displayCloseBtn , centered, 
		thridBtnAction, thirdBtnAutoClosed,thridBtnTitle){
	
	this.modalEle = targetEle;
	
	this.modalEle.querySelector('.modal-title').textContent = title;
	this.modalEle.querySelector('.modal-body').innerHTML = this.convertNToBr(body);
	
	this.modalDialogEle = this.modalEle.querySelector('.modal-dialog');
	
	switch (btnCount) {
	  case 3 : {
		this.thirdBtnTag = this.modalEle.querySelector('.-thrid-btn');
		this.thirdBtnTag.textContent = thridBtnTitle;
		this.injectBtnAction(this.thirdBtnTag, targetObj, thridBtnAction,thirdBtnAutoClosed );
	  }
	  case 2 : {
		this.secondBtnTag = this.modalEle.querySelector('.-second-btn');
		this.secondBtnTag.textContent = secondBtnTitle;
		this.injectBtnAction(this.secondBtnTag, targetObj, secondBtnAction,secondBtnAutoClosed );
	  }
	  case 1 : {
		this.firstBtnTag = this.modalEle.querySelector('.-first-btn');
		this.firstBtnTag.textContent = firstBtnTitle;
		this.injectBtnAction(this.firstBtnTag, targetObj, firstBtnAction,firstBtnAutoClosed );
		break;
	  }
	  default : {
		  throw Error("現在ボタンの数が0以下の場合はサポートしておりません。");
	  }
	}
	
	// set modal position to vertically center
	if(centered == true){
		this.modalDialogEle.classList.add(this.centerdClassName);
	}else{
		this.modalDialogEle.classList.remove(this.centerdClassName);
	}
	
	if(displayCloseBtn == true){
		this.modalEle.querySelector('.btn-close').style.display = "block";
	}else{
		this.modalEle.querySelector('.btn-close').style.display = "none";
	}
	
	// 表示する。
	targetEle.show();
}

/*
 * モーダルのボタンのActionを入れる。
 * @param {document} targetBtnEle 対象のボタン
 * @param {bootstrap} targetObj 表示・非表示を行う。
 * @param {function} action ボタンをクリックした時の挙動。nullの場合はクローズだけを行う。
 * @param {boolean} autoClosed true : action実行後自動クローズ。 false : action実行後自動クローズを行わないモーダルを閉じたい場合は手動でfirstBtnActionに宣言する必要がある。
 */
ModalCommon.prototype.injectBtnAction = function(targetBtnEle, targetObj, action , autoClosed){
	// 既存のEvent削除
	targetBtnEle.removeAttribute('onclick');
	if(action != null){
		if(autoClosed == true){
			console.log("firstBtnAutoClosed : true");
			targetBtnEle.addEventListener('click',function(){
				firstBtnAction();
				targetObj.hide();
			})
		}else{
			console.log("firstBtnAutoClosed : false");
			targetBtnEle.addEventListener('click',action);
		}
	} else{
		targetBtnEle.addEventListener('click',function(){
			targetObj.hide();
		})
	}
}

