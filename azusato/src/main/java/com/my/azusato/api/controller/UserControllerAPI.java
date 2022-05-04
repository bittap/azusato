package com.my.azusato.api.controller;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.my.azusato.api.controller.request.AddNonMemberUserAPIRequest;
import com.my.azusato.api.service.UserServiceAPI;
import com.my.azusato.api.service.request.AddNonMemberUserServiceAPIRequest;
import com.my.azusato.exception.AlreadyExistNonMemberException;
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
@RequestMapping(value = Api.COMMON_REQUSET + Api.USER_CONTROLLER_REQUSET)
@RequiredArgsConstructor
@Slf4j
public class UserControllerAPI {

	private final MessageSource messageSource;

	private final UserProperty userProperty;

	private final UserServiceAPI userAPIService;

	/**
	 * if cookie exists throw, not exists add a nonmember by req.
	 * 
	 * @param req             requestbody nullAble
	 * @param nonmemberCookie if exists throw, not exists add a nonmember.
	 * @param servletRequest  {@link HttpServletRequest}
	 * @param servletResponse {@link HttpServletResponse}
	 */
	@ResponseStatus(HttpStatus.CREATED)
	@PostMapping(value = "add/nonmember")
	public void addNonMember(@RequestBody(required = false) AddNonMemberUserAPIRequest req,
			@CookieValue(value = CookieConstant.NON_MEMBER_KEY, required = false) Cookie nonmemberCookie,
			HttpServletRequest servletRequest, HttpServletResponse servletResponse) {
		log.debug("{}#addNonMember START, req : {}", UserControllerAPI.class.getName(), req);
		if (nonmemberCookie != null) {
			throw new AlreadyExistNonMemberException(servletRequest.getLocale(), messageSource);
		} else {
			String id = getNonMemberRandomString();
			AddNonMemberUserServiceAPIRequest serviceReq = AddNonMemberUserServiceAPIRequest.builder()
					.name(req.getName()).profileImageBase64(req.getProfileImageBase64())
					.profileImageType(req.getProfileImageType()).id(id).build();
			long savedNo = userAPIService.addNonMember(serviceReq);

			Cookie nonMemberCookie = createNonmemberCookie(String.valueOf(savedNo));
			servletResponse.addCookie(nonMemberCookie);

			log.debug("{}#addNonMember END, cookie {} : {}", UserControllerAPI.class.getName(),
					nonMemberCookie.getName(), nonMemberCookie.getValue());

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
