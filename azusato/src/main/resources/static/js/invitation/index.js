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
  .start()

  // Do something when the countdown ends
  .ifEnded(() => {
    
  });

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

//setInterval(function(){
//	const countDowns = getCountDown(WEDDING_INVITATION_DATE);
//	days_ele.textContent = countDowns.days;
//	hours_ele.textContent = countDowns.hours;
//	minutes_ele.textContent = countDowns.minutes;
//	seconds_ele.textContent = countDowns.seconds;
//},1000);

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

function changeImageSrc(src){
	changedImgEle.src = src;
}


function getSeconds(date){
	return date.getTime() / 1000; 
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

const $ = str => document.querySelector(str);
const $$ = str => document.querySelectorAll(str);

(function() {
    if (!window.app) {
        window.app = {};
    }
    app.carousel = {
        removeClass: function(el, classname='') {
            if (el) {
                if (classname === '') {
                    el.className = '';
                } else {
                    el.classList.remove(classname);
                }
                return el;
            }
            return;
        },
        reorder: function() {
            let childcnt = $("#carousel").children.length;
            let childs = $("#carousel").children;

            for (let j=0; j< childcnt; j++) {
                childs[j].dataset.pos = j;
            }
        },
        move: function(el) {
            let selected = el;

            if (typeof el === "string") {
            console.log(`got string: ${el}`);
                selected = (el == "next") ? $(".selected").nextElementSibling : $(".selected").previousElementSibling;
                console.dir(selected);
            }

            let curpos = parseInt(app.selected.dataset.pos);
            let tgtpos = parseInt(selected.dataset.pos);

            let cnt = curpos - tgtpos;
            let dir = (cnt < 0) ? -1 : 1;
            let shift = Math.abs(cnt);

            for (let i=0; i<shift; i++) {
                let el = (dir == -1) ? $("#carousel").firstElementChild : $("#carousel").lastElementChild;

                if (dir == -1) {
                    el.dataset.pos = $("#carousel").children.length;
                    $('#carousel').append(el);
                } else {
                    el.dataset.pos = 0;
                    $('#carousel').prepend(el);
                }

                app.carousel.reorder();
            }


            app.selected = selected;
            let next = selected.nextElementSibling;// ? selected.nextElementSibling : selected.parentElement.firstElementChild;
            var prev = selected.previousElementSibling; // ? selected.previousElementSibling : selected.parentElement.lastElementChild;
            var prevSecond = prev ? prev.previousElementSibling : selected.parentElement.lastElementChild;
            var nextSecond = next ? next.nextElementSibling : selected.parentElement.firstElementChild;

            selected.className = '';
            selected.classList.add("selected");

            app.carousel.removeClass(prev).classList.add('prev');
            app.carousel.removeClass(next).classList.add('next');

            app.carousel.removeClass(nextSecond).classList.add("nextRightSecond");
            app.carousel.removeClass(prevSecond).classList.add("prevLeftSecond");

            app.carousel.nextAll(nextSecond).forEach(item=>{ item.className = ''; item.classList.add('hideRight') });
            app.carousel.prevAll(prevSecond).forEach(item=>{ item.className = ''; item.classList.add('hideLeft') });

        },
        nextAll: function(el) {
            let els = [];

            if (el) {
                while (el = el.nextElementSibling) { els.push(el); }
            }

            return els;

        },
        prevAll: function(el) {
            let els = [];

            if (el) {
                while (el = el.previousElementSibling) { els.push(el); }
            }


            return els;
        },
        keypress: function(e) {
            switch (e.which) {
                case 37: // left
                    app.carousel.move('prev');
                    break;

                case 39: // right
                    app.carousel.move('next');
                    break;

                default:
                    return;
            }
            e.preventDefault();
            return false;
        },
        select: function(e) {
        console.log(`select: ${e}`);
            let tgt = e.target;
            while (!tgt.parentElement.classList.contains('carousel')) {
                tgt = tgt.parentElement;
            }

            app.carousel.move(tgt);

        },
        previous: function(e) {
            app.carousel.move('prev');
        },
        next: function(e) {
            app.carousel.move('next');
        },
        doDown: function(e) {
        console.log(`down: ${e.x}`);
            app.carousel.state.downX = e.x;
        },
        doUp: function(e) {
        console.log(`up: ${e.x}`);
            let direction = 0,
                velocity = 0;

            if (app.carousel.state.downX) {
                direction = (app.carousel.state.downX > e.x) ? -1 : 1;
                velocity = app.carousel.state.downX - e.x;

                if (Math.abs(app.carousel.state.downX - e.x) < 10) {
                    app.carousel.select(e);
                    return false;
                }
                if (direction === -1) {
                    app.carousel.move('next');
                } else {
                    app.carousel.move('prev');
                }
                app.carousel.state.downX = 0;
            }
        },
        init: function() {
            document.addEventListener("keydown", app.carousel.keypress);
           // $('#carousel').addEventListener("click", app.carousel.select, true);
            $("#carousel").addEventListener("mousedown", app.carousel.doDown);
            $("#carousel").addEventListener("touchstart", app.carousel.doDown);
            $("#carousel").addEventListener("mouseup", app.carousel.doUp);
            $("#carousel").addEventListener("touchend", app.carousel.doup);

            app.carousel.reorder();
            $('#prev').addEventListener("click", app.carousel.previous);
            $('#next').addEventListener("click", app.carousel.next);
            app.selected = $(".selected");

        },
        state: {}

    }
    app.carousel.init();
})();

function getDay(seconds){
	return calculateCountDown(seconds, "D");
}

function getHours(seconds){
	return calculateCountDown(seconds, "H");
}

function getMinutes(seconds){
	return calculateCountDown(seconds, "S");
}