package com.my.azusato.api.controller;

import java.net.URISyntaxException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.my.azusato.api.controller.request.ModifyUserProfileAPIRequest;
import com.my.azusato.api.controller.response.DefaultRandomProfileResponse;
import com.my.azusato.api.service.ProfileServiceAPI;
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
@RequestMapping(value = Api.COMMON_REQUSET)
@Slf4j
@RequiredArgsConstructor
public class ProfileControllerAPI {

	public static final String COMMON_URL = "profile";
	
	public static final String RANDOM_URL = COMMON_URL + "/random";
	
	private final ProfileServiceAPI profileService;

	/**
	 * 既に登録したランダムイメージの情報を取得する。
	 * 
	 * @return {@link DefaultRandomProfileResponse}
	 * @throws URISyntaxException ラスパスよりURLを生成する時、不正なURLの場合
	 */
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	@GetMapping(value = RANDOM_URL)
	public DefaultRandomProfileResponse getDefaultRandomProfile() throws URISyntaxException {
		log.debug("[ランダムイメージ取得] START");
		DefaultRandomProfileResponse response = new DefaultRandomProfileResponse();

		response.setProfileImagePath(profileService.getDefaultProfilePath());

		log.debug("[ランダムイメージ取得] END response : {}", response);
		return response;
	}
	
	
	/**
	 * ネーム、プロフィールイメージ情報を更新する。
	 * <ul>
	 * 	<li>200 : 成功</li>
	 * 	<li>400 : ログインしたユーザが存在しない</li>
	 *  <li>500 : 更新対象のユーザが存在しない</li>
	 * </ul>
	 */
	@PutMapping
	public ResponseEntity<Void> updateUserProfile(@RequestBody @Validated ModifyUserProfileAPIRequest req) {
//		if(SessionUtil.isLoginSession(httpSession) == false) {
//			ErrorResponse errorResponse = new ErrorResponse(AzusatoException.I0002,
//					messageSource.getMessage(AzusatoException.I0002, null, servletRequest.getLocale()));
//			ResponseEntity<Object> response = ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
//			log.debug("[セッション情報が存在しない], response : {}",response);
//		}else {
//			LoginUserDto loginUserDto = SessionUtil.getLoginSession(httpSession);
//			ModifyUserProfileServiceAPIRequest serviceReq = ModifyUserProfileServiceAPIRequest.builder()
//							.name(req.getName())
//							.profileImageBase64(req.getProfileImageBase64()).profileImageType(req.getProfileImageType())
//							.userNo(loginUserDto.getNo())
//							.build();
//			
//			profileServiceAPI.updateUserProfile(serviceReq,servletRequest.getLocale());
//		}
		
		return ResponseEntity.ok(null);
	}

	
}
