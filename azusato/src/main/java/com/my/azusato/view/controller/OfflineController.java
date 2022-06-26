package com.my.azusato.view.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.my.azusato.view.controller.common.UrlConstant;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequestMapping(value = { UrlConstant.OFFLINE_CONTROLLER_REQUSET,})
@Controller
@RequiredArgsConstructor
@Slf4j
public class OfflineController {
	
	@GetMapping
	public String initalize()  {
		return "offline";
	}
}
