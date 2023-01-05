package com.my.azusato.view.controller.response;

import lombok.Data;

/**
 * This is used in header.html
 * 
 * @author Carmel
 *
 */
@Data
public class HeaderReponse {

  /**
   * true : home view, false : not home
   */
  boolean home;

  boolean wedding;

  /**
   * true : celebration view, false : not celebration
   */
  boolean celebration;

  /**
   * true : user view, false : not user
   */
  boolean user;
}
