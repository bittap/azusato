package com.my.azusato.view.controller.response;

import com.my.azusato.view.controller.CelebrationController;

import lombok.Data;

/**
 * {@link CelebrationController#write()}に対するレスポンス
 * 
 * @author kim-t
 *
 */
@Data
public class CelebrationWriteResponse {

	/**
	 * プロフィールbase64
	 */
	private String ImageBase64;

	/**
	 * プロフィールイメージタイプ
	 */
	private String ImageType;
}
