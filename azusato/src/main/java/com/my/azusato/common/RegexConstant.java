package com.my.azusato.common;

public class RegexConstant {

  private RegexConstant() {

  }

  /**
   * image/png or image/jpeg のみ容認
   */
  public final static String profileImageBase64 = "^(image\\/png|image\\/jpeg)$";
}
