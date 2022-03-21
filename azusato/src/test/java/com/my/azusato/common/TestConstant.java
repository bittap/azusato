package com.my.azusato.common;

import org.springframework.validation.BindingResult;

/**
 * declare variable for test 
 * @author Carmel
 *
 */
public class TestConstant {

	public final static String HOST = "192.168.0.1";
	
	/**
	 * Stirng of korean of LOCALE 
	 */
	public final static String LOCALE_KO_STR = "ko";
	
	/**
	 * Stirng of Japanese of LOCALE 
	 */
	public final static String LOCALE_JA_STR = "ja";
	
	/**
	 * default size of the model which is excluded from user's model.
	 * spring model size include {@link BindingResult}.
	 */
	public final static int SPRING_MODEL_SIZE = 1;
}
