'use strict';

class Carousel {
  constructor(el) {
    this.el = el;
    this.carouselOptions = ['previous', 'play', 'next'];
    this.carouselData = [];
    this.carouselInView = [];
    for(let i = 1; i<=18; i++){
    	this.carouselData.push({
            'id': i,
            'src': '/image/wedding/'+i+'.jpg'
    	})
    	this.carouselInView.push(i);
    }
    this.carouselContainer;
    this.carouselPlayState;
    this.startX = 0;
    this.movedDistance = 30;
    this.mainItemNumber = 3;
    this.keepStartXHandler = this.keepStartX.bind(this);
    this.triggerNextOrPreviousHandler = this.triggerNextOrPrevious.bind(this);
  }

  async mounted() {
    await this.setupCarousel();
    this.previewImage();
  }
  
  // Build carousel html
  async setupCarousel() {
    const container = document.createElement('div');
    const controls = document.createElement('div');

    // Add container for carousel items and controls
    this.el.append(container, controls);
    container.className = 'carousel-container';
    controls.className = 'carousel-controls';
    
    for(let i = 0; i < this.carouselData.length; i++){
    	const item = this.carouselData[i];
    	const carouselItem = item.src ? document.createElement('img') : document.createElement('div');

        // Add item attributes
        const isWide = await this.isWide(item.src);
        const itemNumber = i+1;
        const classItemName = `carousel-item-${itemNumber}`;
        carouselItem.className = `carousel-item ${classItemName}`;
        carouselItem.src = item.src;
        carouselItem.setAttribute('data-isWide', isWide);
        carouselItem.setAttribute('loading', 'lazy');
        container.append(carouselItem);
        
        if(this.isMainImage(itemNumber)){
      	  this.addEventMoveWithSwipe(carouselItem);
        }
    }
    
    this.carouselOptions.forEach((option) => {
      const btn = document.createElement('button');
      const axSpan = document.createElement('span');

      // Add accessibilty spans to button
      axSpan.innerText = option;
      axSpan.className = 'ax-hidden';
      btn.append(axSpan);

      // Add button attributes
      btn.className = `carousel-control carousel-control-${option}`;
      btn.setAttribute('data-name', option);

      // Add carousel control options
      controls.append(btn);
    });

    // After rendering carousel to our DOM, setup carousel controls' event listeners
    this.setControls([...controls.children]);
    // Set container property
    this.carouselContainer = container;
  }
  
  isWide(src){
	  return new Promise((resolve, reject) => {
		 const img = new Image();
		 img.onload = () => {
			 let isWide = false;
			 if(img.width > img.height){
				 isWide = true;
			 }else{
				 isWide = false;
			 }
			 resolve(isWide);
		 }
		 img.onerror = reject;
		 img.src = src
	  });
  }
  
  isMainImage(itemNumber){
	  return itemNumber == this.mainItemNumber ? true : false;
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
      console.log("remove swipe")
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
  
  previewImage(){
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
const exampleCarousel = new Carousel(el);
// Setup carousel and methods
exampleCarousel.mounted();

