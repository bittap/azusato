package com.my.azusato.locale;

import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * constant relation to locale
 * @author Carmel
 *
 */
public class LocaleConstant {

	/**
	 * supported locale map in this system.
	 */
	public static final Map<String,Locale> supportedLocaleMap = Map.of("ko",Locale.KOREAN, "ja",Locale.JAPANESE);
	
	/**
	 * supported locale list in this system.
	 */
	public static  final List<Locale> supportedLocales = List.of(Locale.JAPANESE,Locale.KOREAN);
	
	/**
	 * if locale is not resolved either path or header, resolve this
	 */
	public static  final Locale DEFAULT_LOCALE = Locale.JAPANESE;
}
