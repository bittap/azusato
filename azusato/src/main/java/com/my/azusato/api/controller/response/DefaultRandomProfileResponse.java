package com.my.azusato.api.controller.response;

import com.my.azusato.api.controller.ProfileControllerAPI;

import lombok.Data;

/**
 * {@link ProfileControllerAPI#write()}に対するレスポンス
 * 
 * @author kim-t
 *
 */
@Data
public class DefaultRandomProfileResponse {

	/**
	 * プロフィール
	 */
	private String profileImagePath;
}
