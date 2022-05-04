package com.my.azusato.view.controller.common;

import com.my.azusato.view.controller.CelebrationController;

/**
 * constant of URL
 * 
 * @author Carmel
 *
 */
public class UrlConstant {

	/**
	 * Url of request mapping of japanese controller
	 */
	public static final String JAPANESE_CONTROLLER_REQUEST = "/ja";

	/**
	 * Url of request mapping of korean controller
	 */
	public static final String KOREAN_CONTROLLER_REQUEST = "/ko";

	public static final String INDEX_CONTROLLER_REQUSET = "/";

	/**
	 * mapping url for {@link CelebrationController}
	 */
	public static final String CELEBRATION_CONTROLLER_REQUSET = "/celebration";

	public static class Api {
		public static final String COMMON_REQUSET = "api/";

		public static final String USER_CONTROLLER_REQUSET = "user/";

		public static final String CELEBRATION_CONTROLLER_REQUSET = "celebration/";

		public static final String PROFILE_CONTROLLER_REQUSET = "profile/";

		public static final String RANDOM_PROFILE_URL = "randomProfile";
	}
}
