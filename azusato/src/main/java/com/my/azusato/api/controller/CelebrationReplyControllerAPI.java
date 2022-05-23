package com.my.azusato.api.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.my.azusato.api.controller.request.AddCelebrationReplyAPIReqeust;
import com.my.azusato.api.service.CelebrationReplyServiceAPI;
import com.my.azusato.dto.LoginUserDto;
import com.my.azusato.exception.AzusatoException;
import com.my.azusato.util.SessionUtil;
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
@RequiredArgsConstructor
@Slf4j
public class CelebrationReplyControllerAPI {

	private final MessageSource messageSource;

	private final HttpSession httpSession;

	private final CelebrationReplyServiceAPI celeReplyAPIService;

	private final HttpServletRequest servletRequest;
	
	public static final String COMMON_URL = "celebration-reply";
	
	public static final String POST_URL = COMMON_URL + "/{celebrationNo}";

	public static final String DELETE_URL = COMMON_URL + "/{celebrationNo}";
	
	/**
	 * 「お祝い書き込み」とお祝いに関わる人に対して「お祝い通知」を登録する。deletedをtrueに変更
	 * <ul>
	 * 	<li>200 : お祝い書き込み成功</li>
	 * 	<li>400 : <br>ユーザ情報が存在しない<br>パラメータがエラー</li>
	 *  <li>401 : ログインしていない</li>
	 * </ul>
	 * @param req パラメータ
	 */
	@ResponseStatus(HttpStatus.CREATED)
	@PostMapping(COMMON_URL)
	public void add(@RequestBody @Validated AddCelebrationReplyAPIReqeust req, @PathVariable(name = "celebrationNo", required = true) Long celebationNo) {
		LoginUserDto loginInfo = SessionUtil.getLoginSession(httpSession).orElseThrow(()->{
			throw new AzusatoException(HttpStatus.UNAUTHORIZED, AzusatoException.I0001,
					messageSource.getMessage(AzusatoException.I0001, null, servletRequest.getLocale()));
		});
		
		celeReplyAPIService.addCelebartionReply(req, celebationNo , loginInfo.getNo(), servletRequest.getLocale());
	}
	
	/**
	 * 「お祝い書き込み」を論理削除する。deletedをtrueに変更
	 * <ul>
	 * 	<li>200 : 論理削除成功</li>
	 * 	<li>400 : <br>対象データ存在なし<br>生成したユーザではない場合<br>パラメータがエラー</li>
	 *  <li>401 : ログインしていない</li>
	 * </ul>
	 * @param celebationNo お祝い番号
	 */
	@ResponseStatus(HttpStatus.OK)
	@DeleteMapping(DELETE_URL)
	public void delete(@PathVariable(name = "celebrationNo", required = true) Long celebationNo) {
		LoginUserDto loginInfo = SessionUtil.getLoginSession(httpSession).orElseThrow(()->{
			throw new AzusatoException(HttpStatus.UNAUTHORIZED, AzusatoException.I0001,
					messageSource.getMessage(AzusatoException.I0001, null, servletRequest.getLocale()));
		});

		celeReplyAPIService.deleteCelebartionReply(celebationNo, loginInfo.getNo(),servletRequest.getLocale());
	}
}