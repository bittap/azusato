function googleTranslateElementInit2() {
	new google.translate.TranslateElement(
		{
			pageLanguage: language,
			autoDisplay: true,
			includedLanguages: 'ko,ja',
			layout: google.translate.TranslateElement.InlineLayout.HORIZONTAL
			//layout: google.translate.TranslateElement.InlineLayout.SIMPLE
		}, 'google_translate_element');
}

