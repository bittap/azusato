package com.my.azusato.api.controller;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Random;
import java.util.stream.Collectors;

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
	
	public static final String COMMON_URL = "profile";
	
	public static final String RANDOM_URL = COMMON_URL + "/random";

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

		response.setProfileImagePath(getDefaultProfilePath());

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
	 * 既に登録した基本イメージファイルパスを返す。 
	 * 基本イメージがあるクラスパスの中にあるファイルを全部取得し、その中でランダムでファイルのイメージパスを取得する。
	 * @return html側からアクセスするpath
	 * @throws URISyntaxException クラスパスよりURLを生成する時、不正なURLの場合
	 */
	private String getDefaultProfilePath() throws URISyntaxException  {
		URL url = this.getClass().getClassLoader().getResource("static/image/default/profile");
		File folder = new File(url.toURI());
		log.debug("folder : {}",folder.getAbsolutePath());
		
		String[] files = folder.list();
		int fileCount = files.length;

		int generatedNumber = random.nextInt(fileCount) + 1;
		int resolvedFileIndex = generatedNumber-1;
		
		log.debug("fileName : {}, fileCount : {}, resolvedFileIndex = {}",Arrays.asList(files).stream().collect(Collectors.joining(",")), fileCount,resolvedFileIndex);

		return Paths.get( "/image/default/profile",files[resolvedFileIndex]).toString();
	}
}
