package com.my.azusato.api.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

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
import com.my.azusato.login.LoginUser;
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
	
	public static final String COMMON_URL = "celebration-notice";
	
	public static final String CELEBRATION_NOTICES_URL = "celebration-notices";
	
	public static final String READCOUNTUP_URL = COMMON_URL + "/read" + "/{celebrationNo}";
	
	private final CelebrationNoticeServiceAPI celeNotiAPIService;
	
	private final HttpServletRequest servletRequest;

	
	@ResponseStatus(HttpStatus.OK)
	@GetMapping(CELEBRATION_NOTICES_URL)
	@MethodAnnotation(description = "API_cel-noti_001 お祝い通知リスト情報取得")
	public List<GetCelebrationNoticesSerivceAPIResponse> celebrationNotices() {
		MyPageControllerRequest page = new MyPageControllerRequest(1, 3, 5);
			
		GetCelebrationsSerivceAPIRequset serviceReq = GetCelebrationsSerivceAPIRequset.builder()
								.pageReq(page)
								.build();
		
		return celeNotiAPIService.celebrationNotices(serviceReq);
	}
	
	/**
	 * 「お祝い」の参照回数
	 * <ul>
	 * 	<li>200 : 参照回数更新成功</li>
	 * 	<li>400 : <br>対象データ存在なし</li>
	 * </ul>
	 * @param celebationNo お祝い番号
	 */
	@ResponseStatus(HttpStatus.OK)
	@PutMapping(READCOUNTUP_URL)
	@MethodAnnotation(description = "API_cel-noti_002 お祝い通知既読処理")
	public void readCountUp(@PathVariable(name = "celebrationNo", required = true) Long celebationNo, @AuthenticationPrincipal LoginUser loginUser) {
		celeNotiAPIService.read(celebationNo, loginUser, servletRequest.getLocale());
	}
}
