package com.my.azusato.api.controller;

import java.util.Objects;

import javax.servlet.http.HttpServletRequest;

import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.my.azusato.annotation.MethodAnnotation;
import com.my.azusato.api.controller.request.MyPageControllerRequest;
import com.my.azusato.api.service.CelebrationNoticeServiceAPI;
import com.my.azusato.api.service.request.GetCelebrationsSerivceAPIRequset;
import com.my.azusato.api.service.response.GetCelebrationNoticesSerivceAPIResponse;
import com.my.azusato.exception.AzusatoException;
import com.my.azusato.login.LoginUser;
import com.my.azusato.property.CelebrationNoticeProperty;
import com.my.azusato.view.controller.common.UrlConstant.Api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * お祝い通知関連API
 * @author Carmel
 *
 */
@RestController
@RequestMapping(value = Api.COMMON_REQUSET)
@RequiredArgsConstructor
@Slf4j
public class CelebrationNoticeControllerAPI {
	
	private final MessageSource messageSource;
	
	public static final String COMMON_URL = "celebration-notice";
	
	public static final String CELEBRATION_NOTICES_URL = COMMON_URL + "s";
	
	public static final String READCOUNTUP_URL = COMMON_URL + "/read" + "/{celebrationNo}";
	
	private final CelebrationNoticeServiceAPI celeNotiAPIService;
	
	private final HttpServletRequest servletRequest;
	
	private final CelebrationNoticeProperty noticeProperty;
	
	
	@ResponseStatus(HttpStatus.OK)
	@GetMapping(CELEBRATION_NOTICES_URL)
	@MethodAnnotation(description = "API_cel-noti_001 お祝い通知リスト情報取得")
	public GetCelebrationNoticesSerivceAPIResponse celebrationNotices(@AuthenticationPrincipal LoginUser loginUser) {
		if(Objects.isNull(loginUser))
			throw new AzusatoException(HttpStatus.UNAUTHORIZED, AzusatoException.I0001,
					messageSource.getMessage(AzusatoException.I0001, null, servletRequest.getLocale()));
		
		final int CURRENT_PAGE = 1;
		MyPageControllerRequest page = MyPageControllerRequest.builder()
										.currentPageNo(CURRENT_PAGE)
										.pageOfElement(noticeProperty.getPageOfElement())
										.build();
		
		GetCelebrationsSerivceAPIRequset serviceReq = GetCelebrationsSerivceAPIRequset.builder()
								.pageReq(page)
								.build();
		
		return celeNotiAPIService.celebrationNotices(serviceReq,loginUser.getUSER_NO());
	}
	
	/**
	 * 「お祝い」の参照回数
	 * <ul>
	 * 	<li>200 : 参照回数更新成功</li>
	 * 	<li>400 : <br>対象データ存在なし</li>
	 *  <li>401 : ログインしていない</li>
	 * </ul>
	 * @param celebationNo お祝い番号
	 */
	@ResponseStatus(HttpStatus.OK)
	@PutMapping(READCOUNTUP_URL)
	@MethodAnnotation(description = "API_cel-noti_002 お祝い通知既読処理")
	public void read(@PathVariable(name = "celebrationNo", required = true) Long celebationNo, @AuthenticationPrincipal LoginUser loginUser) {
		if(Objects.isNull(loginUser))
			throw new AzusatoException(HttpStatus.UNAUTHORIZED, AzusatoException.I0001,
					messageSource.getMessage(AzusatoException.I0001, null, servletRequest.getLocale()));
		
		celeNotiAPIService.read(celebationNo, loginUser.getUSER_NO(), servletRequest.getLocale());
	}
}
