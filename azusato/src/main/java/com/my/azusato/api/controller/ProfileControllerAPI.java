package com.my.azusato.api.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Random;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.my.azusato.api.controller.response.DefaultRandomProfileResponse;
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
@RequestMapping(value = Api.COMMON_REQUSET + Api.PROFILE_CONTROLLER_REQUSET)
@Slf4j
@RequiredArgsConstructor
public class ProfileControllerAPI {

	private final Random random = new Random();

	private final ProfileProperty profileProperty;

	/**
	 * 既に登録したランダムイメージの情報を取得する。
	 * 
	 * @return {@link DefaultRandomProfileResponse}
	 * @throws IOException 既に登録した基本イメージが指定した位置に存在しない場合。
	 */
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	@GetMapping(value = Api.RANDOM_PROFILE_URL)
	public DefaultRandomProfileResponse getDefaultRandomProfile() throws IOException {
		log.debug("[ランダムイメージ取得] START");
		DefaultRandomProfileResponse response = new DefaultRandomProfileResponse();

		response.setProfileImageType(profileProperty.getDefaultImageType());
		response.setProfileImageBase64(getDefaultProfileBase64());

		log.debug("[ランダムイメージ取得] END response : {}", response);
		return response;

	}

	/**
	 * 既に登録した基本イメージのbase64ファイルを読み込んで、取得する。
	 * 基本イメージのURIはsrc/main/resources/default/profile/base64/avatar(Random数字).txt
	 * 
	 * @return 既に登録した基本イメージのbase64
	 * @throws IOException ファイルが存在しない場合
	 */
	private String getDefaultProfileBase64() throws IOException {
		// 例) profileProperty.getDefaultMaxNumber()が3の場合
		// 1~3まで取得
		int generatedNumber = random.nextInt(profileProperty.getDefaultMaxNumber()) + 1;
		Path path = Paths.get("src/main/resources/default/profile/base64", "avatar" + generatedNumber + ".txt");
		return Files.readString(path);

	}
}
