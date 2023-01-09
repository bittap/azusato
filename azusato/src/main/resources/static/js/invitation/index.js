const changedImgEle = document.querySelector('#changedImage');
const imgEles = document.querySelectorAll('#images img');
const days_ele = document.querySelector('#days');
const hours_ele = document.querySelector('#hours');
const minutes_ele = document.querySelector('#minutes');
const seconds_ele = document.querySelector('#seconds');
const attend_btn_ele = document.querySelector('#attend_form_btn');
const attend_form_ele = document.querySelector('#attend_form');

const opacityHiddenEles = document.querySelectorAll('.opacity-hidden');

const observer = new IntersectionObserver(entries => {
  // 감지한 모든 .card 요소의 정보를 entries 배열로 전달받습니다.
  // entries 배열을 순회해, isIntersecting 조건이 참일 경우 "visible" 이라는 클래스명을 추가합니다.
  entries.forEach(entry => {
    // 요소가 화면에 나타났다면
    if (entry.isIntersecting) {
    	entry.target.classList.add("opacity-visiable");
    	entry.target.classList.remove("opacity-hidden");
    	observer.unobserve(entry.target);
    }
  })
}, { threshold: 0.5 })

opacityHiddenEles.forEach(ele => {
    // 모든 .card 요소의 인터섹션을 감지합니다.
  observer.observe(ele)
});

const TOOLTIP = new bootstrap.Tooltip(document.querySelector('#attend_btn'), {
	trigger:"manual", // プログラムでツールチップが表示されるように
	offset:"0,15",
	container: false,
	fallbackPlacements: ['right'],
	placement: 'right',
	title: WEDDING_TOOLTIP_TITLE
});
TOOLTIP.show();

setInterval(function(){
	const countDowns = getCountDown(WEDDING_INVITATION_DATE);
	days_ele.textContent = countDowns.days;
	hours_ele.textContent = countDowns.hours;
	minutes_ele.textContent = countDowns.minutes;
	seconds_ele.textContent = countDowns.seconds;
},1000);

imgEles.forEach(function(imgEle){
	const src = imgEle.getAttribute('src');
	imgEle.addEventListener('click',function(){
		changeImageSrc(src);
	});
});

attend_btn_ele.addEventListener('click',function(){
	modalCommon.displayTwoBtnModal(ATTEND_MODAL_TITLE,ATTEND_MODAL_BODY,async function(){
		try{
			await attend();
		}catch(e){
			console.log(e);
			modalCommon.displayErrorModal(e.title,e.message);
		}
	},null,null,null,null,false,false);
});

async function attend() {
	const res = await fetch(apiUrl + "/wedding/attender", {
		method:'POST', 
		headers: {
			'Accept': 'application/json',
			'Content-Type': 'application/json',
			'Accept-Language': language,
			'X-CSRF-TOKEN': document.querySelector('[name="_csrf"]').getAttribute('content')
		},
		body: JSON.stringify(Object.fromEntries(new FormData(attend_form_ele)))
	});
	
	if(!res.ok) {
		const result = await res.json();
		return Promise.reject(result);
	}else{
		modalCommon.displayOneBtnModal(ATTEND_MODAL_OK_TITLE,ATTEND_MODAL_OK_BODY,async function(){
			location.reload(false);
		})
	}
}

function changeImageSrc(src){
	changedImgEle.src = src;
}


function getCountDown(dateToStr){
	dateTo = moment(dateToStr);
	dateFrom = moment();
	
	if(dateFrom.isBefore(dateTo)){
		// 結婚式日時 - 今の日時
		const differSeconds = Math.floor(dateTo.diff(dateFrom) / 1000);
		// xxx秒 / 60 x 60 x 24 => xxx日
		const days = getDay(differSeconds);
		// 時分秒
		const hoMiSeOfSeconds = differSeconds - (days.seconds);
		// .時分秒 / 60 x 60 => xxx時間
		const hours = getHours(hoMiSeOfSeconds);
		// 分秒
		const miSeOfSeconds = hoMiSeOfSeconds - (hours.seconds);
		// 分秒 / 60 => xxx分
		const minutes = getMinutes(miSeOfSeconds);
		// 分秒 - xxx分 => 秒 
		const seconds = miSeOfSeconds - (minutes.seconds);
		return {
			"days" : days.result,
			"hours" : hours.result,
			"minutes" : minutes.result,
			"seconds" : seconds
		}
	}else{
		return {
			"days" : 0,
			"hours" : 0,
			"minutes" : 0,
			"seconds" : 0
		}
	}
	

};

function calculateCountDown(seconds,type){
	let mod = null;
	switch (type) {
	case "D":
		mod =  60 * 60 * 24;
		break;
	case "H":
		mod =  60 * 60;
		break;
	case "S":
		mod =  60;
		break;
	default:
		 throw `calculateCountDownのタイプは"D","H","S"以外にはサポートしません。`;
	}
	
	const result = Math.floor(seconds / mod);
	return {
		"result" : result,
		"seconds" : result * mod
	};
}

function getDay(seconds){
	return calculateCountDown(seconds, "D");
}

function getHours(seconds){
	return calculateCountDown(seconds, "H");
}

function getMinutes(seconds){
	return calculateCountDown(seconds, "S");
}