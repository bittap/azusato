CookieCommon = function(){
	
}

/*
 * クッキーを取得する。
 * ない場合は""
 * @return string ある場合 : そのクッキーの値、ない場合 : ""
 * Cookie ex)'_gcl_au=1.1.542726326.1654867794; s_fid=16A191DDB621B4BC-06342DE7EFED21A3; s_nr=1654871809946; _ga=GA1.1.1765576053.1654867794; _ga_SKHZPJHJCP=GS1.1.1654940952.2.0.1654940952.60';
 */
CookieCommon.prototype.getCookie = function(cname){
	let name = cname + "=";
	  let decodedCookie = decodeURIComponent(document.cookie);
	  let ca = decodedCookie.split(';');
	  for(let i = 0; i <ca.length; i++) {
	    let c = ca[i];
	    while (c.charAt(0) == ' ') {
	      c = c.substring(1);
	    }
	    if (c.indexOf(name) == 0) {
	      return c.substring(name.length, c.length);
	    }
	  }
	  return "";
}