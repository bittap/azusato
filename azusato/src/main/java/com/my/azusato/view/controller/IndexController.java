package com.my.azusato.view.controller;

import java.io.IOException;
import java.util.Locale;
import java.util.Objects;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.my.azusato.interceptor.LocaleInterceptor;
import com.my.azusato.login.Grant;
import com.my.azusato.login.LoginUser;
import com.my.azusato.view.controller.common.ModelConstant;
import com.my.azusato.view.controller.common.UrlConstant;
import com.my.azusato.view.controller.response.HeaderReponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequestMapping(value = { UrlConstant.INDEX_CONTROLLER_REQUSET, UrlConstant.JAPANESE_CONTROLLER_REQUEST,
		UrlConstant.KOREAN_CONTROLLER_REQUEST, })
@Controller
@RequiredArgsConstructor
@Slf4j
public class IndexController {
	
	@GetMapping
	public ModelAndView initalize(HttpServletRequest request, HttpServletResponse response,@AuthenticationPrincipal LoginUser loginUser) throws IOException {
		log.debug("index Controller");
		log.debug("login : {}",Objects.nonNull(loginUser));
		
		ModelAndView mav = new ModelAndView();
		String redirectOrView;
		
		// 管理者の場合は、管理者のindexに遷移させる。
		if(Objects.nonNull(loginUser) && Grant.containGrantedAuthority(loginUser.getAuthorities(), Grant.ADMIN_ROLE)) {
			redirectOrView = LocaleInterceptor.getLocale(request) == Locale.KOREAN ? 
					"redirect:" + UrlConstant.KOREAN_CONTROLLER_REQUEST + UrlConstant.COMMON_ADMIN_CONTROLLER_REQUSET
					: "redirect:" + UrlConstant.JAPANESE_CONTROLLER_REQUEST + UrlConstant.COMMON_ADMIN_CONTROLLER_REQUSET;
			
		}else {
			redirectOrView = "index";
		}

		mav.setViewName(redirectOrView);
		// make home icon active
		HeaderReponse hr = new HeaderReponse();
		hr.setHome(true);
		mav.addObject(ModelConstant.HEADER_KEY, hr);

		return mav;
	}
}
