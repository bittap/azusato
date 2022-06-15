ApiCommon = function(){
	// 基本ヘッダー
	this.header = {
	  'Accept': 'application/json',
	  'Content-Type': 'application/json',
	  'Accept-Language': language,
	  'X-CSRF-TOKEN': document.querySelector('[name="_csrf"]').getAttribute('content')
	}
	// 基本ヘッダー
	this.noContentTypeheader = {
	  'Accept-Language': language,
	  'X-CSRF-TOKEN': document.querySelector('[name="_csrf"]').getAttribute('content')
	}
}