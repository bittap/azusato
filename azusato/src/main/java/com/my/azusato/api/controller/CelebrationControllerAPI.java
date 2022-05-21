package com.my.azusato.api.controller;

import java.util.Objects;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.my.azusato.api.controller.request.AddCelebrationAPIReqeust;
import com.my.azusato.api.controller.request.ModifyCelebrationAPIReqeust;
import com.my.azusato.api.service.CelebrationServiceAPI;
import com.my.azusato.api.service.request.AddCelebrationServiceAPIRequest;
import com.my.azusato.api.service.request.GetCelebrationsSerivceAPIRequset;
import com.my.azusato.api.service.request.ModifyCelebationServiceAPIRequest;
import com.my.azusato.api.service.response.GetCelebrationSerivceAPIResponse;
import com.my.azusato.api.service.response.GetCelebrationsSerivceAPIResponse;
import com.my.azusato.dto.LoginUserDto;
import com.my.azusato.entity.UserEntity.Type;
import com.my.azusato.exception.AzusatoException;
import com.my.azusato.util.SessionUtil;
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
	 * お祝い情報を返却する。
	 * @param celebationNo お祝い番号
	 * @return celebationNoより検索されたお祝い情報
	 * @throws AzusatoException 400　データが存在しない場合
	 */
	@ResponseStatus(HttpStatus.OK)
	@GetMapping("/{no}")
	public GetCelebrationSerivceAPIResponse get(@PathVariable(name = "no", required = true) Long celebationNo) {
		return celeAPIService.getCelebration(celebationNo,servletRequest.getLocale());
	}
	
	@ResponseStatus(HttpStatus.OK)
	@GetMapping
	public GetCelebrationsSerivceAPIResponse getCelebrations(@ModelAttribute @Validated MyPageControllerRequest page) {
		// ログインしていない時のuserNo
		final long NO_LOGIN_USER_NO = 0L;
		
		Optional<LoginUserDto> opLoginInfo = SessionUtil.getLoginSession(httpSession);
		
		long userNo = opLoginInfo.isPresent() ? opLoginInfo.get().getNo() : NO_LOGIN_USER_NO;
		
		// もし、現在ページ番号がないと現在ページ番号は１を設定
		if(Objects.isNull(page.getCurrentPageNo())) {
			page.setCurrentPageNo(1);
		}
			
		GetCelebrationsSerivceAPIRequset serviceReq = GetCelebrationsSerivceAPIRequset.builder()
								.loginUserNo(userNo)
								.pageReq(page)
								.build();
		
		return celeAPIService.getCelebrations(serviceReq);
	}

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
	@PostMapping
	public void add(@RequestBody @Validated AddCelebrationAPIReqeust req) {
		LoginUserDto loginInfo = SessionUtil.getLoginSession(httpSession).orElseThrow(()->{
			throw new AzusatoException(HttpStatus.UNAUTHORIZED, AzusatoException.I0001,
					messageSource.getMessage(AzusatoException.I0001, null, servletRequest.getLocale()));
		});
		
		AddCelebrationServiceAPIRequest serviceReq = AddCelebrationServiceAPIRequest.builder()
				.name(req.getName()).profileImageBase64(req.getProfileImageBase64()).profileImageType(req.getProfileImageType())
				.title(req.getTitle()).content(req.getContent()).userNo(loginInfo.getNo()).build();

		if (loginInfo.getUserType().equals(Type.admin.toString())) {
			celeAPIService.addCelebartionAdmin(serviceReq, servletRequest.getLocale());
		} else {
			celeAPIService.addCelebartion(serviceReq, servletRequest.getLocale());
		}
		
	}
	
	@ResponseStatus(HttpStatus.OK)
	@PutMapping
	public void modify(ModifyCelebrationAPIReqeust req) {
		LoginUserDto loginInfo = SessionUtil.getLoginSession(httpSession).orElseThrow(()->{
			throw new AzusatoException(HttpStatus.UNAUTHORIZED, AzusatoException.I0001,
					messageSource.getMessage(AzusatoException.I0001, null, servletRequest.getLocale()));
		});
		
		ModifyCelebationServiceAPIRequest serviceReq = ModifyCelebationServiceAPIRequest.builder()
				.name(req.getName()).profileImageBase64(req.getProfileImageBase64()).profileImageType(req.getProfileImageType())
				.title(req.getTitle()).content(req.getContent()).userNo(loginInfo.getNo()).build();
		
		celeAPIService.modifyCelebartion(serviceReq, servletRequest.getLocale());
	}
}
