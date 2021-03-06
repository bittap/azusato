package com.my.azusato.view.controller;

import java.util.Locale;
import java.util.Objects;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.my.azusato.api.service.CelebrationServiceAPI;
import com.my.azusato.interceptor.LocaleInterceptor;
import com.my.azusato.property.CelebrationProperty;
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
	
	private final CelebrationProperty celeProperty;

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
			@PathVariable(name = "replyNo",required = false) Long celebrationReplyNo) {
		log.debug("redirectListFromNotice controller");
		
		Integer currentPageNo = celeAPIService.getPage(celebrationNo,LocaleInterceptor.getLocale(servletRequest));
		String queryParameter = Objects.nonNull(celebrationReplyNo) ? 
				String.format("celebrationNo=%d&celebrationReplyNo=%d&currentPageNo=%d", celebrationNo,celebrationReplyNo,currentPageNo)
				: String.format("celebrationNo=%d&currentPageNo=%d", celebrationNo,currentPageNo);
		return LocaleInterceptor.getLocale(servletRequest) == Locale.KOREAN ? 
				"redirect:" + UrlConstant.JAPANESE_CONTROLLER_REQUEST + UrlConstant.CELEBRATION_CONTROLLER_REQUSET + "?" + queryParameter
				: "redirect:" + UrlConstant.KOREAN_CONTROLLER_REQUEST + UrlConstant.CELEBRATION_CONTROLLER_REQUSET + "?" + queryParameter;
	}

	/**
	 * ??????????????????????????????????????????
	 */
	@GetMapping(CELEBRATION_WRITE_URL)
	public ModelAndView write() {
		log.debug("[????????????????????????] START");

		ModelAndView mav = new ModelAndView(VIEW_FOLDER_NAME + "write");
		// set header
		addHeaderModel(mav);
		log.debug("[????????????????????????] END response : {}",mav);
		return mav;
	}
	
	/**
	 * ??????????????????????????????????????????
	 */
	@GetMapping(CELEBRATION_MODIFY_URL)
	public ModelAndView modify(@PathVariable(name = "no",required = true) Long celebrationNo) {
		log.debug("[????????????????????????] START");

		ModelAndView mav = new ModelAndView(VIEW_FOLDER_NAME + "write");
		// set header
		addHeaderModel(mav);
		// model response ??????
		mav.addObject(ModelConstant.DATA_KEY, CelebrationModifyResponse.builder().celebrationNo(celebrationNo).build());
		log.debug("[????????????????????????] END response : {}",mav);
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
