package com.my.azusato.view.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.extern.slf4j.Slf4j;

@RequestMapping("/")
@Controller
@Slf4j
public class IndexController {

	@GetMapping
	public String initalize() {
		log.debug("index Controller");
		return "index";
	}
}
