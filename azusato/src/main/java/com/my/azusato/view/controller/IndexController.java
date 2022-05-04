package com.my.azusato.view.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.my.azusato.view.controller.common.ModelConstant;
import com.my.azusato.view.controller.common.UrlConstant;
import com.my.azusato.view.controller.response.HeaderReponse;

import lombok.extern.slf4j.Slf4j;

@RequestMapping(value = { UrlConstant.INDEX_CONTROLLER_REQUSET, UrlConstant.JAPANESE_CONTROLLER_REQUEST,
		UrlConstant.KOREAN_CONTROLLER_REQUEST, })
@Controller
@Slf4j
public class IndexController {

	@GetMapping
	public ModelAndView initalize() {
		log.debug("index Controller");

		ModelAndView mav = new ModelAndView();

		mav.setViewName("index");
		// make home icon active
		HeaderReponse hr = new HeaderReponse();
		hr.setHome(true);
		mav.addObject(ModelConstant.HEADER_KEY, hr);

		return mav;
	}
}
