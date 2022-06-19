package com.my.azusato.api.controller;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.FilenameUtils;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.my.azusato.api.controller.request.ModifyUserProfileAPIRequest;
import com.my.azusato.api.controller.request.UploadProfileImageAPIRequest;
import com.my.azusato.api.controller.response.DefaultRandomProfileResponse;
import com.my.azusato.api.service.ProfileServiceAPI;
import com.my.azusato.api.service.request.ModifyUserProfileServiceAPIRequest;
import com.my.azusato.exception.AzusatoException;
import com.my.azusato.login.LoginUser;
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
	
	public static final String UPLOAD_IMG_URL = COMMON_URL + "/upload-img";
	
	public static final List<String> PERMIT_IMAGE_TYPES = List.of("png","jpeg","jpg");
	
	private final ProfileServiceAPI profileService;
	
	private final MessageSource ms;
	
	private final HttpServletRequest servletRequest;

	/**
	 * 既に登録したランダムイメージの情報を取得する。
	 * 
	 * @return {@link DefaultRandomProfileResponse}
	 */
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	@GetMapping(value = RANDOM_URL)
	public DefaultRandomProfileResponse getDefaultRandomProfile() {
		log.debug("[ランダムイメージ取得] START");
		DefaultRandomProfileResponse response = new DefaultRandomProfileResponse();

		response.setProfileImagePath(profileService.getDefaultProfilePath());

		log.debug("[ランダムイメージ取得] END response : {}", response);
		return response;
	}
	
	/**
	 * イメージのアップロード行う。
	 * <ul>
	 * 	<li>200 : 成功</li>
	 * 	<li>400 : <br>パラメータがnull<br>サポートしない拡張子</li>
	 *  <li>401 : ログインしていない</li>
	 *  <li>500 : その他エラー</li>
	 * </ul>
	 * @param req　リクエストパラメータ
	 * @param loginUser ログインしたユーザ情報
	 * @throws IOException
	 */
	@ResponseStatus(HttpStatus.OK)
	@PostMapping(value =  UPLOAD_IMG_URL, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public void uploadImage(@Validated @ModelAttribute UploadProfileImageAPIRequest req,@AuthenticationPrincipal LoginUser loginUser) throws IOException {
		String extension = FilenameUtils.getExtension(req.getProfileImage().getOriginalFilename());
		// ファイル拡張子チェック
		PERMIT_IMAGE_TYPES.stream().filter((e)->e.equalsIgnoreCase(extension)).findFirst()
				.orElseThrow(()->{
					String profileImageFieldName = ms.getMessage("profileImage", new String[] {}, servletRequest.getLocale());
					throw new AzusatoException(HttpStatus.BAD_REQUEST, AzusatoException.I0004, ms.getMessage("file-type-mismatch", 
							new String[] {extension,profileImageFieldName,PERMIT_IMAGE_TYPES.stream().collect(Collectors.joining(","))}, servletRequest.getLocale()));
				});
		
		if(Objects.isNull(loginUser)) {
			throw new AzusatoException(HttpStatus.UNAUTHORIZED, AzusatoException.I0001,
					ms.getMessage(AzusatoException.I0001, null, servletRequest.getLocale()));
		}
		
		ModifyUserProfileServiceAPIRequest serviceReq = ModifyUserProfileServiceAPIRequest.builder()
				.userNo(loginUser.getUSER_NO())
				.profileImage(req.getProfileImage().getInputStream())
				.profileImageType(extension)
				.build();
		
		
		profileService.updateUserProfile(serviceReq, servletRequest.getLocale());
		
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
