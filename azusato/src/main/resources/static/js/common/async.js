AsyncCommon = function(){
	
}

AsyncCommon.prototype.delay = function(ms){
	return new Promise((resolve)=>setTimeout(function(){
		console.log("delay END");
		resolve();
	},ms));
	
}