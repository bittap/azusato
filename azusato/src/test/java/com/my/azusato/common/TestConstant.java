package com.my.azusato.common;

import java.time.LocalDateTime;
import java.util.Locale;

import org.springframework.validation.BindingResult;

/**
 * declare variable for test
 * 
 * @author Carmel
 *
 */
public class TestConstant {

	public final static String HOST = "192.168.0.1";

	public final static String DEFAULT_CHARSET = "UTF-8";

	public final static String MAKE_ABSOLUTE_URL = "/";

	/**
	 * korean of LOCALE
	 */
	public final static Locale LOCALE_KO = Locale.KOREAN;

	/**
	 * Japanese of LOCALE
	 */
	public final static Locale LOCALE_JA = Locale.JAPANESE;

	/**
	 * Stirng of korean of LOCALE
	 */
	public final static String LOCALE_KO_STR = "ko";

	/**
	 * Stirng of Japanese of LOCALE
	 */
	public final static String LOCALE_JA_STR = "ja";

	/**
	 * size of the model which is excluded from user's model. spring model size
	 * include {@link BindingResult}.
	 */
	public final static int SPRING_MODEL_SIZE = 1;

	/**
	 * for using the test database
	 */
	public final static String PROFILES = "test";

	public final static String EXPECT_XML_FILE_NAME = "expect.xml";

	public final static String INIT_XML_FILE_NAME = "init.xml";

	public final static String[] DEFAULT_EXCLUDE_COLUMNS = { "no", "create_datetime", "update_datetime" };

	public final static String[] DEFAULT_EXCLUDE_DATE_COLUMNS = { "create_datetime", "update_datetime" };
	
	public final static String[] DEFAULT_EXCLUDE_UPDATE_DATE_COLUMNS = { "update_datetime" };

	/**
	 * common location for test data
	 */
	public final static String COMMON_TEST_DATA_FOLDER = "src/test/data/";

	/**
	 * common location for test data
	 */
	public final static String COMMON_ENTITY_FOLDER = COMMON_TEST_DATA_FOLDER + "entity/";

	public static class Entity {
		public static final int[] GET_INDEXS = { 0, 1, 2 };
		public static final String[] createdVarChars = { "varchar1", "varchar2", "varchar3", "varchar4", "varchar5",
				"varchar6" };
		public static final String[] updatedVarChars = { "update1", "update2", "update3", "update4", "update5",
				"update6" };
		public static final int[] createdInts = { 1, 2, 3, 4, 5, 6 };
		public static final long[] createdLongs = { 1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L };
		public static final int[] updatedInts = { 1000, 1001, 1002, 1003, 1004 };
		public static final String[] ProfileImageType = { "image/png", "image/jpeg" };
		public static final String[] ImageType = { "png", "jpeg" };
		public static final boolean CreatedBoolean = true;
		public static final boolean UpdatedBoolean = false;
		public static final LocalDateTime createdDatetime = LocalDateTime.of(2022, 1, 2, 3, 4, 5);
		public static final LocalDateTime[] createdDatetimes = {createdDatetime, LocalDateTime.of(2022, 2, 2, 2, 2, 2)};
		public static final LocalDateTime updatedDatetimeWhenCreate = LocalDateTime.of(2022, 1, 2, 3, 4, 5);
		public static final LocalDateTime updatedDatetime = LocalDateTime.of(2022, 6, 7, 8, 9, 10);
		public static final LocalDateTime[] updatedDatetimes = {updatedDatetime, LocalDateTime.of(2022, 7, 7, 7, 7, 7)};

	}

}
