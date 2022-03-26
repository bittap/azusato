package com.my.azusato.view.controller.response;

import lombok.Data;

/**
 * This is used in header.html
 * @author Carmel
 *
 */
@Data
public class HeaderReponse {

	/**
	 * true : home view, false : not home
	 */
	boolean home;
	
	/**
	 * true : celebration view, false : not celebration
	 */
	boolean celebration;
}
