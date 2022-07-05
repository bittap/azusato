/*
*	/layout/nav.html関連JS
*/

// お祝いメニューは常にツールチップを表示するように
const NAV_CELEBRATION_TOOLTIP = new bootstrap.Tooltip(document.querySelector('#nav_celebration'), {
	trigger:"manual", // プログラムでツールチップが表示されるように
	offset:"0,3",
	container: 'body',
	placement: 'bottom',
});
console.log(NAV_CELEBRATION_TOOLTIP);
NAV_CELEBRATION_TOOLTIP.show();