package com.my.azusato.view.controller.response;

import com.my.azusato.view.controller.CelebrationController;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * {@link CelebrationController#write()}に対するレスポンス
 * 
 * @author kim-t
 *
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CelebrationWriteResponse {

	/**
	 * ユーザ情報のネーム
	 */
	private String name;

	/**
	 * プロフィールimageSrc
	 */
	private String imageSrc;
}
