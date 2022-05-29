package com.my.azusato.api.controller;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.context.MessageSource;
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
import com.my.azusato.property.ProfileProperty;
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

	private final Random random = new Random();

	private final ProfileProperty profileProperty;
	
	private final HttpServletRequest servletRequest;
	
	private final ProfileServiceAPI profileServiceAPI;
	
	private final MessageSource messageSource;
	
	private final HttpSession httpSession;
	
	public static final String COMMON_URL = "profile";
	
	public static final String RANDOM_URL = COMMON_URL + "/random";

	/**
	 * 既に登録したランダムイメージの情報を取得する。
	 * 
	 * @return {@link DefaultRandomProfileResponse}
	 * @throws IOException 既に登録した基本イメージが指定した位置に存在しない場合。
	 */
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	@GetMapping(value = RANDOM_URL)
	public DefaultRandomProfileResponse getDefaultRandomProfile() throws IOException {
		log.debug("[ランダムイメージ取得] START");
		DefaultRandomProfileResponse response = new DefaultRandomProfileResponse();

		response.setProfileImageType(profileProperty.getDefaultImageType());
		response.setProfileImageBase64(getDefaultProfileBase64());

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

	/**
	 * 既に登録した基本イメージのbase64ファイルを読み込んで、取得する。
	 * 基本イメージのURIはclasspath:default/profile/base64/avatar(Random数字).txt
	 * 
	 * @return 既に登録した基本イメージのbase64
	 * @throws IOException ファイルが存在しない場合
	 */
	private String getDefaultProfileBase64() throws IOException {
		// 例) profileProperty.getDefaultMaxNumber()が3の場合
		// 1~3まで取得
		int generatedNumber = random.nextInt(profileProperty.getDefaultMaxNumber()) + 1;
		String path = Paths.get( "default/profile/base64", "avatar" + generatedNumber + ".txt").toString();
		log.debug("absolutePath : {}",Paths.get( "default/profile/base64", "avatar" + generatedNumber + ".txt").toAbsolutePath().toString());
		try(InputStream is = this.getClass().getClassLoader().getResourceAsStream(path); BufferedInputStream bi = new BufferedInputStream(is)){
			return new String(bi.readAllBytes()); 
		}
	}
}
