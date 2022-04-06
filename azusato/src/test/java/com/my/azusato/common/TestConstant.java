package com.my.azusato.common;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

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
	 *  size of the model which is excluded from user's model.
	 * spring model size include {@link BindingResult}.
	 */
	public final static int SPRING_MODEL_SIZE = 1;
	
	/**
	 * for using the test database
	 */
	public final static String PROFILES = "test";
	
	/**
	 * common location for test data
	 */
	public final static String COMMON_TEST_DATA_FOLDER = "src/test/data/";
	
	public static class Entity{
		public static final String[] createdVarChars = {"varchar1","varchar2","varchar3","varchar4","varchar5","varchar6"};
		public static final String[] updatedVarChars = {"update1","update12","update3","update4","update5","update6"};
		public static final String[] ImageType = {"png","jpeg"};
		public static final boolean CreatedBoolean = true;
		public static final boolean UpdatedBoolean = false;
		
		public static final LocalDateTime createdNow = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
		
		public static final LocalDateTime updatedNow = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
	}
	
}
