package com.my.azusato.view.controller;

import java.util.Locale;
import java.util.Objects;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.my.azusato.api.controller.request.MyPageControllerRequest;
import com.my.azusato.api.service.CelebrationServiceAPI;
import com.my.azusato.api.service.request.GetCelebrationsSerivceAPIRequset;
import com.my.azusato.interceptor.LocaleInterceptor;
import com.my.azusato.view.controller.common.ModelConstant;
import com.my.azusato.view.controller.common.UrlConstant;
import com.my.azusato.view.controller.response.CelebrationModifyResponse;
import com.my.azusato.view.controller.response.HeaderReponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Controller for celebration page
 * 
 * @author Carmel
 *
 */
@RequestMapping(value = { UrlConstant.CELEBRATION_CONTROLLER_REQUSET,
		UrlConstant.JAPANESE_CONTROLLER_REQUEST + UrlConstant.CELEBRATION_CONTROLLER_REQUSET,
		UrlConstant.KOREAN_CONTROLLER_REQUEST + UrlConstant.CELEBRATION_CONTROLLER_REQUSET, })
@Controller
@Slf4j
@RequiredArgsConstructor
public class CelebrationController {
	
	private final CelebrationServiceAPI celeAPIService;
	
	private final HttpServletRequest servletRequest;

	private final static String VIEW_FOLDER_NAME = "celebration/";
	
	public static final String CELEBRATION_WRITE_URL = "/write";
	
	public static final String CELEBRATION_MODIFY_URL = "/write/{no}";

	@GetMapping
	public ModelAndView list() {
		log.debug("list controller");

		ModelAndView mav = new ModelAndView(VIEW_FOLDER_NAME + "list");

		// set header
		addHeaderModel(mav);

		return mav;
	}
	
	@GetMapping(value = {"/redirect/list/from-notice/{no}","/redirect/list/from-notice/{no}/{replyNo}"})
	public String redirectListFromNotice(@PathVariable(name = "no",required = true) Long celebrationNo,
			@PathVariable(name = "replyNo",required = false) Long celebrationReplyNo,
			@ModelAttribute @Validated MyPageControllerRequest page) {
		log.debug("redirectListFromNotice controller");
		
		// もし、現在ページ番号がないと現在ページ番号は１を設定
		if(Objects.isNull(page.getCurrentPageNo())) {
			page.setCurrentPageNo(1);
		}
			
		GetCelebrationsSerivceAPIRequset serviceReq = GetCelebrationsSerivceAPIRequset.builder()
								.pageReq(page)
								.build();
		
		int currentPageNo = celeAPIService.getPage(serviceReq).getCurrentPageNo();
		String queryParameter = String.format("celebrationNo=%d&celebrationReplyNo=%d&currentPageNo=%d", celebrationNo,celebrationReplyNo,currentPageNo);
		return LocaleInterceptor.getLocale(servletRequest) == Locale.KOREAN ? 
				"redirect:" + UrlConstant.JAPANESE_CONTROLLER_REQUEST + UrlConstant.CELEBRATION_CONTROLLER_REQUSET + "?" + queryParameter
				: "redirect:" + UrlConstant.KOREAN_CONTROLLER_REQUEST + UrlConstant.CELEBRATION_CONTROLLER_REQUSET + "?" + queryParameter;
	}

	/**
	 * お祝い投稿ページを表示する。
	 */
	@GetMapping(CELEBRATION_WRITE_URL)
	public ModelAndView write() {
		log.debug("[お祝い投稿ページ] START");

		ModelAndView mav = new ModelAndView(VIEW_FOLDER_NAME + "write");
		// set header
		addHeaderModel(mav);
		log.debug("[お祝い投稿ページ] END response : {}",mav);
		return mav;
	}
	
	/**
	 * お祝い投稿ページを表示する。
	 */
	@GetMapping(CELEBRATION_MODIFY_URL)
	public ModelAndView modify(@PathVariable(name = "no",required = true) Long celebrationNo) {
		log.debug("[お祝い修正ページ] START");

		ModelAndView mav = new ModelAndView(VIEW_FOLDER_NAME + "write");
		// set header
		addHeaderModel(mav);
		// model response 設定
		mav.addObject(ModelConstant.DATA_KEY, CelebrationModifyResponse.builder().celebrationNo(celebrationNo).build());
		log.debug("[お祝い修正ページ] END response : {}",mav);
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
		hr.setCelebration(true);
		mav.addObject(ModelConstant.HEADER_KEY, hr);
	}
}
