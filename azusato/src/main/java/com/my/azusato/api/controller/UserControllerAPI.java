package com.my.azusato.api.controller;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.my.azusato.api.controller.request.AddNonMemberUserAPIRequest;
import com.my.azusato.api.service.UserServiceAPI;
import com.my.azusato.api.service.request.AddNonMemberUserServiceAPIRequest;
import com.my.azusato.exception.AlreadyExistNonMemberException;
import com.my.azusato.property.UserProperty;
import com.my.azusato.view.controller.common.CookieConstant;
import com.my.azusato.view.controller.common.UrlConstant;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping(value = UrlConstant.API_CONTROLLER_REQUSET + UrlConstant.USER_CONTROLLER_REQUSET)
@RequiredArgsConstructor
@Slf4j
public class UserControllerAPI {

	private final MessageSource messageSource;

	private final UserProperty userProperty;

	private final UserServiceAPI userAPIService;

	@ResponseStatus(HttpStatus.CREATED)
	@PostMapping("add/nonmember")
	public void addNonMember(AddNonMemberUserAPIRequest req,
			@CookieValue(value = CookieConstant.NON_MEMBER_KEY, required = false) Cookie nonmemberCookie,
			HttpServletResponse res) {
		log.debug("{}#addNonMember START, req : {}", UserControllerAPI.class.getName(), req);
		if (nonmemberCookie != null) {
			throw new AlreadyExistNonMemberException(res.getLocale(), messageSource);
		} else {
			String id = getNonMemberRandomString();
			AddNonMemberUserServiceAPIRequest serviceReq = AddNonMemberUserServiceAPIRequest.builder()
					.name(req.getName()).profileImageBase64(req.getProfileImageBase64())
					.profileImageType(req.getProfileImageType()).id(id).build();
			userAPIService.addNonMember(serviceReq);

		}
	}

	private String getNonMemberRandomString() {
		return RandomStringUtils.random(userProperty.getNonMemberIdLength());
	}

}
