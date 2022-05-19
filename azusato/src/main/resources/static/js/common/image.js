ImageCommon = function(){
	
}

/*
 * base64データにてイメージ更新する。
 */
ImageCommon.prototype.changeImageSrcBase64 = function(tag, imageType, imageBase64){
	tag.setAttribute("src",`data: ${imageType} ;base64,${imageBase64}`);
}