package com.my.azusato.view.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.my.azusato.view.controller.common.ModelConstant;
import com.my.azusato.view.controller.common.UrlConstant;
import com.my.azusato.view.controller.response.HeaderReponse;

import lombok.extern.slf4j.Slf4j;

@RequestMapping(value = { UrlConstant.COMMON_ADMIN_CONTROLLER_REQUSET + UrlConstant.INDEX_CONTROLLER_REQUSET, 
		 UrlConstant.COMMON_ADMIN_CONTROLLER_REQUSET +UrlConstant.JAPANESE_CONTROLLER_REQUEST,
		 UrlConstant.COMMON_ADMIN_CONTROLLER_REQUSET +UrlConstant.KOREAN_CONTROLLER_REQUEST, })
@Controller
@Slf4j
public class AdminIndexController {
	
	public static final String FOLDER_NAME = "admin";

	@GetMapping
	public ModelAndView initalize() {
		log.debug("admin index Controller");

		ModelAndView mav = new ModelAndView();

		mav.setViewName(FOLDER_NAME + "index");
		// make home icon active
		HeaderReponse hr = new HeaderReponse();
		hr.setHome(true);
		mav.addObject(ModelConstant.HEADER_KEY, hr);

		return mav;
	}
}
