const korean = language === "ko" ? true : false;

const changedImgEle = document.querySelector('#changedImage');
const imgEles = document.querySelectorAll('#images img');
const days_ele = document.querySelector('#days');
const hours_ele = document.querySelector('#hours');
const minutes_ele = document.querySelector('#minutes');
const seconds_ele = document.querySelector('#seconds');
const attend_btn_ele = document.querySelector('#attend_form_btn');
const attend_form_ele = document.querySelector('#attend_form');
const opacityHiddenEles = document.querySelectorAll('.opacity-hidden');

//Unix timestamp (in seconds) to count down to
var twoDaysFromNow = (new Date().getTime() / 1000) + (86400 * 2) + 1;

// Set up FlipDown
var flipdown = new FlipDown(getSeconds(new Date(WEDDING_INVITATION_DATE))) //
  // Start the countdown
  .start();

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

imgEles.forEach(function(imgEle){
	const src = imgEle.getAttribute('src');
	imgEle.addEventListener('click',function(){
		changeImageSrc(src);
	});
});

setControlAttendRadio();

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
	const entries = Object.fromEntries(new FormData(attend_form_ele));
	
	if(!/\S/.test(entries.remark)){
		entries.remark = null;
	}
	
	const res = await fetch(apiUrl + "/wedding/attender", {
		method:'POST', 
		headers: {
			'Accept': 'application/json',
			'Content-Type': 'application/json',
			'Accept-Language': language,
			'X-CSRF-TOKEN': document.querySelector('[name="_csrf"]').getAttribute('content')
		},
		body: JSON.stringify(entries)
	});
	
	if(!res.ok) {
		const result = await res.json();
		return Promise.reject(result);
	}else{
		modalCommon.displayOneBtnModal(ATTEND_MODAL_OK_TITLE,ATTEND_MODAL_OK_BODY,async function(){
			location.reload();
		})
	}
}

function setControlAttendRadio(){
	const attendRadioBtns = document.querySelectorAll(".form-control input[type='radio'][name='attend']")
	const attenderNumberSel = document.querySelector(".form-control [name='attenderNumber']");
	const addedFamilyArea = document.querySelector("#attend_form #addedFamilyArea");
	const options = [...attenderNumberSel.options];

	for (let radioBtn of attendRadioBtns) {
		if(radioBtn.value == "true"){
			radioBtn.addEventListener('change',function(){
				console.log('〇ボタンに変更')
				if(korean){
					addedFamilyArea.style.display = "block";
				}
				const selectedValue = "1";
				options.forEach(option => {
					if(selectedValue == option.value){
						option.selected = true;
					}
				});
			});
		}else{
			radioBtn.addEventListener('change',function(){
				console.log('✖ボタンに変更')
				if(korean){
					addedFamilyArea.style.display = "none";
				}
				const selectedValue = "0";
				options.forEach(option => {
					if(selectedValue == option.value){
						option.selected = true;
					}
				});
			});
		}
	}
}

function changeImageSrc(src){
	changedImgEle.src = src;
}


function getSeconds(date){
	return date.getTime() / 1000; 
}
