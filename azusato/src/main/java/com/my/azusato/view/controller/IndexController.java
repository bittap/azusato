package com.my.azusato.view.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.my.azusato.view.controller.common.UrlConstant;

import lombok.extern.slf4j.Slf4j;

@RequestMapping(value = {
		UrlConstant.INDEX_CONTROLLER_REQUSET,
		UrlConstant.INDEX_CONTROLLER_REQUSET+UrlConstant.JAPANESE_CONTROLLER_REQUEST,
		UrlConstant.INDEX_CONTROLLER_REQUSET+UrlConstant.KOREAN_CONTROLLER_REQUEST,
		})
@Controller
@Slf4j
public class IndexController {

	@GetMapping
	public String initalize() {
		log.debug("index Controller");
		return "index";
	}
}
