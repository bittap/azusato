package com.my.azusato.api.controller;

import java.util.Objects;

import javax.servlet.http.HttpSession;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.my.azusato.view.controller.common.SessionConstant;
import com.my.azusato.view.controller.common.UrlConstant.Api;

import lombok.RequiredArgsConstructor;

/**
 * API for user.
 * <ul>
 * <li>add nonmember.</li>
 * </ul>
 * 
 * @author kim-t
 *
 */
@RestController
@RequestMapping(value = Api.COMMON_REQUSET)
@RequiredArgsConstructor
public class SessionControllerAPI {

	private final HttpSession httpSession;
	
	public static final String COMMON_URL = "session";
	
	public static final String CHECK_URL = COMMON_URL + "/checked-login-session";
	
	/**
	 * ログインセッション情報があるかどうかチェック
	 * 
	 * @return true : ある , false : ない
	 */
	@GetMapping(CHECK_URL)
	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	public boolean check() {
		return Objects.nonNull(httpSession.getAttribute(SessionConstant.LOGIN_KEY))? true : false;
	}

}
