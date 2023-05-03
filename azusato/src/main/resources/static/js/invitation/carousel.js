'use strict';

class Carousel {
  constructor(el) {
    this.el = el;
    this.carouselOptions = ['previous', 'play', 'next'];
    this.carouselData = [];
    this.carouselInView = [];
    this.carouselContainer = document.querySelector('.carousel-container');;
    this.carouselPlayState;
    this.startX = 0;
    this.movedDistance = 30;
    this.keepStartXHandler = this.keepStartX.bind(this);
    this.triggerNextOrPreviousHandler = this.triggerNextOrPrevious.bind(this);
  }

  async mounted() {
    await this.setupCarousel();
    this.setPreviewImage();
  }
  
  // Build carousel html
  async setupCarousel() {
    const controls = document.querySelector('.carousel-controls');
    
    // ボタンに機能を追加
    this.setControls([...controls.children]);

    for(let i = 1; i <=this.carouselContainer.children.length; i++){
    	const children = this.carouselContainer.children[i-1];
    	this.carouselData.push({
            'id': i,
            'src': children.src
    	})
    	this.carouselInView.push(i);
    	
        if(this.isMainImage(i)){
        	this.addEventMoveWithSwipe(children);
        }
    }
  }
  
  isMainImage(itemNumber){
	  return itemNumber == 3 ? true : false;
  }
  
  setControls(controls) {
    controls.forEach(control => {
      control.onclick = (event) => {
        event.preventDefault();

        // Manage control actions, update our carousel data first then with a callback update our DOM
        this.controlManager(control.dataset.name);
      };
    });
  }

  controlManager(control) {
    if (control === 'previous') return this.previous();
    if (control === 'next') return this.next();
    if (control === 'add') return this.add();
    if (control === 'play') return this.play();

    return;
  }
  
  removeEventMoveWithSwipe(ele){
	  ele.removeEventListener('touchstart',this.keepStartXHandler);
	  ele.removeEventListener('touchend',this.triggerNextOrPreviousHandler);
  }

  addEventMoveWithSwipe(ele){
	  ele.addEventListener('touchstart',this.keepStartXHandler);
	  ele.addEventListener('touchend',this.triggerNextOrPreviousHandler);
  }
  
  keepStartX(event) {
    let touch = event.changedTouches[0];
    this.startX = touch.screenX;
  }
  
  triggerNextOrPrevious(event){
  	let touch = event.changedTouches[0];
    let endX =  touch.screenX;
    if(this.startX + this.movedDistance <= endX) {
      this.next();
      event.preventDefault();
    }else if(this.startX - this.movedDistance >= endX){
      this.previous();
      event.preventDefault();
    }
  }
  
  previous() {
    // Update order of items in data array to be shown in carousel
    this.carouselData.unshift(this.carouselData.pop());

    // Push the first item to the end of the array so that the previous item is front and center
    this.carouselInView.push(this.carouselInView.shift());

    this.rotateImg();
  }

  next() {
    // Update order of items in data array to be shown in carousel
    this.carouselData.push(this.carouselData.shift());
    
    // Take the last item and add it to the beginning of the array so that the next item is front and center
    this.carouselInView.unshift(this.carouselInView.pop());

    this.rotateImg();
  }
  
  rotateImg(){
    // Update the css class for each carousel item in view
    this.carouselInView.forEach((itemNumber, index) => {
      const ele =  this.carouselContainer.children[index];
      ele.className = `carousel-item carousel-item-${itemNumber}`;
      this.removeEventMoveWithSwipe(ele);
      if(this.isMainImage(itemNumber)){
      	  this.addEventMoveWithSwipe(ele);
      }
    });
  }

  play() {
    const playBtn = document.querySelector('.carousel-control-play');
    const startPlaying = () => this.next();

    if (playBtn.classList.contains('playing')) {
      // Remove class to return to play button state/appearance
      playBtn.classList.remove('playing');

      // Remove setInterval
      clearInterval(this.carouselPlayState); 
      this.carouselPlayState = null; 
    } else {
      // Add class to change to pause button state/appearance
      playBtn.classList.add('playing');

      // First run initial next method
      this.next();

      // Use play state prop to store interval ID and run next method on a 1.5 second interval
      this.carouselPlayState = setInterval(startPlaying, 1500);
    };
  }
  
  setPreviewImage(){
	const body = document.querySelector('body');
	const preventScrollClassName = "no_scroll";
    const imagePriviewModal = document.querySelector('#imagePriviewModal');
    const previewImg = imagePriviewModal.querySelector('#imagePriviewImg');

    let imgs = document.querySelectorAll('.carousel img');
    imgs.forEach(function(img){
      img.onclick = function(){
    	  body.classList.add(preventScrollClassName);
          previewImg.src = this.src;
          previewImg.setAttribute('data-isWide', this.getAttribute('data-isWide'));
          
          imagePriviewModal.style.display = "block";
      }
    })

    imagePriviewModal.onclick = function(){
    	body.classList.remove(preventScrollClassName);
        imagePriviewModal.style.display = "none";
    }
    
  }

}

// Refers to the carousel root element you want to target, use specific class selectors if using multiple carousels
const el = document.querySelector('.carousel');
// Create a new carousel object
const carousel = new Carousel(el);
// Setup carousel and methods
carousel.mounted();

