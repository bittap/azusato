package com.my.azusato.view.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/jquery")
public class JsController {

	@GetMapping
	public String get() {
		System.out.println("Jquery module get");
		return "node_modules/jquery/dist/jquery.js";
	}
}
