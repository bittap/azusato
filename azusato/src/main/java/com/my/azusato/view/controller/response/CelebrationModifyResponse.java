package com.my.azusato.view.controller.response;

import com.my.azusato.view.controller.CelebrationController;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * {@link CelebrationController#modify(Long)}に対するレスポンス
 * 
 * @author kim-t
 *
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CelebrationModifyResponse {

	/**
	 * お祝い番号
	 */
	private Long celebrationNo;
}
