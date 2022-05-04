package com.my.azusato.view.controller;

import java.util.Objects;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;

import com.my.azusato.api.controller.response.DefaultRandomProfileResponse;
import com.my.azusato.api.controller.response.UserAPIResponse;
import com.my.azusato.dto.LoginUserDto;
import com.my.azusato.view.controller.common.CookieConstant;
import com.my.azusato.view.controller.common.ModelConstant;
import com.my.azusato.view.controller.common.SessionConstant;
import com.my.azusato.view.controller.common.UrlConstant;
import com.my.azusato.view.controller.common.UrlConstant.Api;
import com.my.azusato.view.controller.response.CelebrationWriteResponse;
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

	private final static String VIEW_FOLDER_NAME = "celebration/";

	private final RestTemplate restTemplate;

	private final HttpSession httpSession;

	@GetMapping
	public ModelAndView list() {
		log.debug("list controller");

		ModelAndView mav = new ModelAndView(VIEW_FOLDER_NAME + "list");

		// set header
		addHeaderModel(mav);

		return mav;
	}

	@GetMapping("/write")
	public ModelAndView write(
			@CookieValue(value = CookieConstant.NON_MEMBER_KEY, required = false) Cookie nonmemberCookie) {
		log.debug("list controller");

		Object loginInfoObj = httpSession.getAttribute(SessionConstant.LOGIN_KEY);
		CelebrationWriteResponse writeResponse = new CelebrationWriteResponse();
		// ログインした状態
		if (Objects.nonNull(loginInfoObj) || Objects.isNull(nonmemberCookie)) {
			// getLoginInfo
			long userNo = Objects.nonNull(loginInfoObj) ? ((LoginUserDto) loginInfoObj).getNo()
					: Long.valueOf(nonmemberCookie.getValue());
			// 非会員ユーザ
			UserAPIResponse user = new UserAPIResponse();

			writeResponse.setImageBase64(user.getImageBase64());
			writeResponse.setImageType(user.getImageType());
		} else {
			// random基本イメージを取得
			DefaultRandomProfileResponse randomImageResponse = restTemplate.getForObject(
					Api.COMMON_REQUSET + Api.PROFILE_CONTROLLER_REQUSET + Api.RANDOM_PROFILE_URL,
					DefaultRandomProfileResponse.class);

			writeResponse.setImageBase64(randomImageResponse.getImageBase64());
			writeResponse.setImageType(randomImageResponse.getImageType());
		}
		// 存在しないとランダムプロフィールを取得。

		ModelAndView mav = new ModelAndView(VIEW_FOLDER_NAME + "write");
		// set data
		mav.addObject(ModelConstant.DATA_KEY, writeResponse);
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
		hr.setCelebration(true);
		mav.addObject(ModelConstant.HEADER_KEY, hr);
	}
}
