package com.my.azusato.view.controller.common;

import com.my.azusato.view.controller.CelebrationController;
import com.my.azusato.view.controller.UserController;

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

  public static final String SCRIPT_TEST_CONTROLLER_REQUSET = "/test/script";

  /**
   * mapping url for {@link CelebrationController}
   */
  public static final String CELEBRATION_CONTROLLER_REQUSET = "/celebration";

  public static final String WEDDING_CONTROLLER_REQUSET = "/wedding";

  public static final String OFFLINE_CONTROLLER_REQUSET = "/offline";

  /**
   * mapping url for {@link UserController}
   */
  public static final String USER_CONTROLLER_REQUSET = "/user";

  public static final String COMMON_ADMIN_CONTROLLER_REQUSET = "/admin";

  public static final String WEDDING_ADMIN_CONTROLLER_REQUSET = "/admin/wedding";

  public static class Api {
    public static final String COMMON_REQUSET = "api/";
  }
}
