package com.my.azusato.view.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.my.azusato.view.controller.common.ModelConstant;
import com.my.azusato.view.controller.common.UrlConstant;
import com.my.azusato.view.controller.response.HeaderReponse;

import lombok.extern.slf4j.Slf4j;

@RequestMapping(value = { UrlConstant.USER_CONTROLLER_REQUSET,
		UrlConstant.JAPANESE_CONTROLLER_REQUEST + UrlConstant.USER_CONTROLLER_REQUSET,
		UrlConstant.KOREAN_CONTROLLER_REQUEST + UrlConstant.USER_CONTROLLER_REQUSET, })
@Controller
@Slf4j
public class UserController {
	
	private final static String VIEW_FOLDER_NAME = "user/";
	
	public static final String USER_LOGIN_URL = "/login";

	@GetMapping(USER_LOGIN_URL)
	public ModelAndView login() {
		log.debug("list controller");

		ModelAndView mav = new ModelAndView(VIEW_FOLDER_NAME + "login");

		// set header
		addHeaderModel(mav);

		return mav;
	}
	
	/**
	 * set a header model for activating celebration menu
	 * 
	 * @param mav ModelAndView
	 */
	private void addHeaderModel(ModelAndView mav) {
		// make home icon active
		HeaderReponse hr = new HeaderReponse();
		hr.setUser(true);
		mav.addObject(ModelConstant.HEADER_KEY, hr);
	}
}
