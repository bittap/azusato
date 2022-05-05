package com.my.azusato.api.controller;

import java.util.Objects;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.my.azusato.api.controller.request.AddCelebrationAPIReqeust;
import com.my.azusato.api.service.CelebrationServiceAPI;
import com.my.azusato.api.service.request.AddCelebrationServiceAPIRequest;
import com.my.azusato.dto.LoginUserDto;
import com.my.azusato.entity.UserEntity.Type;
import com.my.azusato.exception.AzusatoException;
import com.my.azusato.view.controller.common.SessionConstant;
import com.my.azusato.view.controller.common.UrlConstant.Api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * API for the celebration.
 * <ul>
 * <li>add a celebration.</li>
 * </ul>
 * 
 * @author kim-t
 *
 */
@RestController
@RequestMapping(value = Api.COMMON_REQUSET + Api.CELEBRATION_CONTROLLER_REQUSET)
@RequiredArgsConstructor
@Slf4j
public class CelebrationControllerAPI {

	private final MessageSource messageSource;

	private final HttpSession httpSession;

	private final CelebrationServiceAPI celeAPIService;

	private final HttpServletRequest servletRequest;

	/**
	 * add a celebration. The first, check session. if session exists check user
	 * Type is admin, then isAdmin = true. if session doesn't exist throw error
	 * 
	 * 
	 * @param req             requestbody, Validated
	 * @param nonmemberCookie nonmember cookie
	 * @param servletRequest  for message
	 */
	@ResponseStatus(HttpStatus.CREATED)
	@PostMapping(value = "add")
	public void addCelebartion(@RequestBody @Validated AddCelebrationAPIReqeust req) {
		log.debug("{}#addNonMember START, req : {}, nonmemberCookie : {}", CelebrationControllerAPI.class.getName(),
				req);

		Object loginInfoObj = httpSession.getAttribute(SessionConstant.LOGIN_KEY);

		if (Objects.nonNull(loginInfoObj)) {
			LoginUserDto loginInfo = (LoginUserDto) loginInfoObj;
			AddCelebrationServiceAPIRequest serviceReq = AddCelebrationServiceAPIRequest.builder().title(req.getTitle())
					.content(req.getContent()).userNo(loginInfo.getNo()).build();

			if (loginInfo.getUserType().equals(Type.admin.toString())) {
				celeAPIService.addCelebartionAdmin(serviceReq, servletRequest.getLocale());
			} else {
				celeAPIService.addCelebartion(serviceReq, servletRequest.getLocale());
			}
		} else {
			throw new AzusatoException(HttpStatus.UNAUTHORIZED, AzusatoException.I0001,
					messageSource.getMessage(AzusatoException.I0001, null, servletRequest.getLocale()));
		}
	}
}
