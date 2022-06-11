package com.my.azusato.api.controller;

import java.util.Objects;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.my.azusato.api.controller.request.AddNonMemberUserAPIRequest;
import com.my.azusato.api.service.UserServiceAPI;
import com.my.azusato.api.service.request.AddNonMemberUserServiceAPIRequest;
import com.my.azusato.api.service.response.GetSessionUserServiceAPIResponse;
import com.my.azusato.exception.AzusatoException;
import com.my.azusato.exception.ErrorResponse;
import com.my.azusato.login.LoginUser;
import com.my.azusato.property.UserProperty;
import com.my.azusato.view.controller.common.CookieConstant;
import com.my.azusato.view.controller.common.UrlConstant.Api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

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
@Slf4j
public class UserControllerAPI {

	private final MessageSource messageSource;

	private final UserProperty userProperty;

	private final UserServiceAPI userAPIService;

	private final HttpServletRequest servletRequest;

	private final HttpServletResponse servletResponse;

	public static final String COMMON_URL = "user";
	
	public static final String ADD_NONMEMBER_URL = COMMON_URL + "/nonmember";
	

	/**
	 * ログインしたユーザのテーブル情報を返す。
	 * <ul>
	 * <li>200 : ログイン情報取得に成功</li>
	 * <li>400 : ログインしていない</li>
	 * </ul>
	 * 
	 * @param loginUser ログインしたユーザ情報
	 * @return ユーザ情報
	 */
	@GetMapping(COMMON_URL)
	public ResponseEntity<Object> getLoginUser(@AuthenticationPrincipal LoginUser loginUser) {
		
		if(Objects.nonNull(loginUser)) {
			GetSessionUserServiceAPIResponse responseBody = userAPIService.getSessionUser(loginUser.getUSER_NO(),
					servletRequest.getLocale());
			ResponseEntity<Object> response = ResponseEntity.ok(responseBody);
			log.debug("[ログイン情報が存在する] END, response : {}",response);
			return response;
		}else {
			ErrorResponse errorResponse = new ErrorResponse(AzusatoException.I0002,
					messageSource.getMessage(AzusatoException.I0002, null, servletRequest.getLocale()));
			ResponseEntity<Object> response = ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
			log.debug("[ログイン情報が存在しない] END, response : {}",response);
			return response;
		}
	}

	/**
	 * if cookie exists throw, not exists add a nonmember by req.
	 * 
	 * @param req             requestbody
	 * @param nonmemberCookie if exists throw, not exists add a nonmember.
	 * @param servletResponse {@link HttpServletResponse}
	 */
	@ResponseStatus(HttpStatus.CREATED)
	@PostMapping(ADD_NONMEMBER_URL)
	public void addNonMember(@RequestBody @Validated AddNonMemberUserAPIRequest req,
			@CookieValue(value = CookieConstant.NON_MEMBER_KEY, required = false) Cookie nonmemberCookie) {
		log.debug("{}#addNonMember START, req : {}", UserControllerAPI.class.getName(), req);
		if (nonmemberCookie != null) {
			log.debug("[既に存在する非会員ユーザ] cookieNo : {}", nonmemberCookie.getValue());
			throw new AzusatoException(HttpStatus.BAD_REQUEST, AzusatoException.I0003,
					messageSource.getMessage(AzusatoException.I0003, null, servletRequest.getLocale()));
		} else {
			String id = getNonMemberRandomString();
			AddNonMemberUserServiceAPIRequest serviceReq = AddNonMemberUserServiceAPIRequest.builder()
					.name(req.getName()).id(id).build();
			String savedUsername = userAPIService.addNonMember(serviceReq);

			Cookie nonMemberCookie = createNonmemberCookie(String.valueOf(savedUsername));
			// セッションの有効pathこれを設定しないとこのURL以外には認識されない。
			nonMemberCookie.setPath("/");
			servletResponse.addCookie(nonMemberCookie);

			log.debug("[非会員ユーザcookie追加], cookie {} : {}", nonMemberCookie.getName(), nonMemberCookie.getValue());
		}
	}

	/**
	 * create a cookie by id.
	 * 
	 * @param userNo value of cookie
	 * @return key : {@link CookieConstant#NON_MEMBER_KEY}, value : id
	 */
	private Cookie createNonmemberCookie(String userNo) {
		Cookie cookie = new Cookie(CookieConstant.NON_MEMBER_KEY, userNo);
		cookie.setMaxAge(userProperty.getNonMemberCookieMaxTime());

		return cookie;
	}

	/**
	 * create random alphabetic strings.
	 * 
	 * @return random strings
	 */
	private String getNonMemberRandomString() {
		return RandomStringUtils.randomAlphabetic(userProperty.getNonMemberIdLength());
	}

}
