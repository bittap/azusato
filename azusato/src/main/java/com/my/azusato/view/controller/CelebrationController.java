package com.my.azusato.view.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;

import com.my.azusato.api.controller.response.DefaultRandomProfileResponse;
import com.my.azusato.api.service.response.GetSessionUserServiceAPIResponse;
import com.my.azusato.exception.AzusatoException;
import com.my.azusato.exception.ErrorResponse;
import com.my.azusato.view.controller.common.ModelConstant;
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

	@GetMapping
	public ModelAndView list() {
		log.debug("list controller");

		ModelAndView mav = new ModelAndView(VIEW_FOLDER_NAME + "list");

		// set header
		addHeaderModel(mav);

		return mav;
	}

	/**
	 * セッションユーザ情報APIを呼び出す。もし、ある場合は、そのユーザの情報を返す。ない場合はランダムプロフィールAPIを呼び出し、その情報を返す。
	 * 
	 * @return {@link CelebrationWriteResponse}と{@link HeaderReponse}
	 */
	@GetMapping("/write")
	public ModelAndView write() {
		log.debug("write controller");

		ResponseEntity<Object> resSessionUserInfo = restTemplate
				.getForEntity(Api.COMMON_REQUSET + Api.USER_CONTROLLER_REQUSET, Object.class);

		CelebrationWriteResponse writeResponse = new CelebrationWriteResponse();

		// ユーザ情報取得成功
		if (resSessionUserInfo.getStatusCode() == HttpStatus.OK) {
			GetSessionUserServiceAPIResponse resBody = (GetSessionUserServiceAPIResponse) resSessionUserInfo.getBody();
			writeResponse.setName(resBody.getName());
			writeResponse.setImageBase64(resBody.getProfileImageBase64());
			writeResponse.setImageType(resBody.getProfileImageType());
			// セッションなし
		} else if (resSessionUserInfo.getStatusCode() == HttpStatus.NOT_FOUND) {
			// random基本イメージを取得
			DefaultRandomProfileResponse randomImageResponse = restTemplate.getForObject(
					Api.COMMON_REQUSET + Api.PROFILE_CONTROLLER_REQUSET + Api.RANDOM_PROFILE_URL,
					DefaultRandomProfileResponse.class);

			writeResponse.setImageBase64(randomImageResponse.getImageBase64());
			writeResponse.setImageType(randomImageResponse.getImageType());
			// エラー
		} else {
			ErrorResponse resBody = (ErrorResponse) resSessionUserInfo.getBody();
			throw new AzusatoException(resSessionUserInfo.getStatusCode(), resBody.getTitle(), resBody.getMessage());
		}

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
