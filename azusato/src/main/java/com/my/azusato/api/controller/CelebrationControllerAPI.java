package com.my.azusato.api.controller;

import java.io.IOException;
import java.util.Objects;

import javax.servlet.http.HttpServletRequest;

import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.my.azusato.api.controller.request.AddCelebrationAPIReqeust;
import com.my.azusato.api.controller.request.ModifyCelebrationAPIReqeust;
import com.my.azusato.api.controller.request.MyPageControllerRequest;
import com.my.azusato.api.service.CelebrationServiceAPI;
import com.my.azusato.api.service.request.AddCelebrationServiceAPIRequest;
import com.my.azusato.api.service.request.GetCelebrationsSerivceAPIRequset;
import com.my.azusato.api.service.request.ModifyCelebationServiceAPIRequest;
import com.my.azusato.api.service.response.GetCelebrationContentSerivceAPIResponse;
import com.my.azusato.api.service.response.GetCelebrationSerivceAPIResponse;
import com.my.azusato.api.service.response.GetCelebrationsSerivceAPIResponse;
import com.my.azusato.exception.AzusatoException;
import com.my.azusato.login.Grant;
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
@RequiredArgsConstructor
@Slf4j
public class CelebrationControllerAPI {
	
	private final MessageSource messageSource;

	private final CelebrationServiceAPI celeAPIService;

	private final HttpServletRequest servletRequest;
	
	public static final String COMMON_URL = "celebration";
	
	public static final String CELEBRATION_URL = COMMON_URL + "/{celebrationNo}";
	
	public static final String CELEBRATION_CONTENT_URL = COMMON_URL + "/content" + "/{celebrationNo}";
	
	public static final String PUT_URL = COMMON_URL + "/{celebrationNo}";
	
	public static final String DELETE_URL = COMMON_URL + "/{celebrationNo}";
	
	public static final String CELEBRATIONS_URL = "celebrations";
	
	public static final String READCOUNTUP_URL = COMMON_URL + "/read-count-up" + "/{celebrationNo}";
	
	/**
	 * お祝い情報を返却する。
	 * <ul>
	 * 	<li>200 : お祝い番号によるお祝いデータ</li>
	 * 	<li>400 : <br>テーブルにお祝いデータが存在しない。<br>パラメータがLong以外</li>
	 * </ul>
	 * @param celebationNo お祝い番号
	 * @return celebationNoより検索されたお祝い情報
	 */
	@ResponseStatus(HttpStatus.OK)
	@GetMapping(CELEBRATION_URL)
	public GetCelebrationSerivceAPIResponse celebation(@PathVariable(name = "celebrationNo", required = true) Long celebationNo) {
		return celeAPIService.getCelebration(celebationNo,servletRequest.getLocale());
	}
	
	/**
	 * お祝い情報と書き込み情報を返却する。
	 * <ul>
	 * 	<li>200 : 成功</li>
	 * 	<li>400 : <br>テーブルにお祝いデータが存在しない。<br>パラメータがLong以外</li>
	 * </ul>
	 * @param celebationNo お祝い番号
	 * @param loginUser ログインしたユーザ情報
	 * @return celebationNoより検索されたお祝い情報
	 */
	@ResponseStatus(HttpStatus.OK)
	@GetMapping(CELEBRATION_CONTENT_URL)
	public GetCelebrationContentSerivceAPIResponse celebationContent(@PathVariable(name = "celebrationNo", required = true) Long celebationNo,
			@AuthenticationPrincipal LoginUser loginUser) {
		// ログインしていない時のuserNo
		final long NO_LOGIN_USER_NO = 0L;
		
		long userNo = Objects.nonNull(loginUser) ? loginUser.getUSER_NO() : NO_LOGIN_USER_NO;
		
		return celeAPIService.getCelebrationContent(celebationNo,userNo,servletRequest.getLocale());
	}
	
	@ResponseStatus(HttpStatus.OK)
	@GetMapping(CELEBRATIONS_URL)
	public GetCelebrationsSerivceAPIResponse celebrations(@ModelAttribute @Validated MyPageControllerRequest page) {

		// もし、現在ページ番号がないと現在ページ番号は１を設定
		if(Objects.isNull(page.getCurrentPageNo())) {
			page.setCurrentPageNo(1);
		}
			
		GetCelebrationsSerivceAPIRequset serviceReq = GetCelebrationsSerivceAPIRequset.builder()
								.pageReq(page)
								.build();
		
		return celeAPIService.getCelebrations(serviceReq);
	}

	/**
	 * お祝い情報を修正する。
	 * <ul>
	 * 	<li>200 : お祝い情報修正成功</li>
	 * 	<li>400 : <br>対象データ存在なし<br>生成したユーザではない場合<br>パラメータがエラー</li>
	 *  <li>401 : ログインしていない</li>
	 *  <li>500 : その他エラー</li>
	 * </ul>
	 * @param req パラメータ
	 * @param loginUser ログインしたユーザ情報
	 * @throws IOException プロフィールイメージの書き込み失敗
	 */
	@ResponseStatus(HttpStatus.CREATED)
	@PostMapping(COMMON_URL)
	public void add(@RequestBody @Validated AddCelebrationAPIReqeust req, @AuthenticationPrincipal LoginUser loginUser ) throws IOException {
		if(Objects.isNull(loginUser)) {
			throw new AzusatoException(HttpStatus.UNAUTHORIZED, AzusatoException.I0001,
					messageSource.getMessage(AzusatoException.I0001, null, servletRequest.getLocale()));
		}

		AddCelebrationServiceAPIRequest serviceReq = AddCelebrationServiceAPIRequest.builder()
				.name(req.getName())
				.title(req.getTitle()).content(req.getContent()).userNo(loginUser.getUSER_NO()).build();

		if (Grant.containGrantedAuthority(loginUser.getAuthorities(), Grant.ADMIN_ROLE)) {
			celeAPIService.addCelebartionAdmin(serviceReq, servletRequest.getLocale());
		} else {
			celeAPIService.addCelebartion(serviceReq, servletRequest.getLocale());
		}
		
	}
	
	/**
	 * お祝い情報を修正する。
	 * <ul>
	 * 	<li>200 : お祝い情報修正成功</li>
	 * 	<li>400 : <br>対象データ存在なし<br>生成したユーザではない場合<br>パラメータがエラー</li>
	 *  <li>401 : ログインしていない</li>
	 *  <li>500 : プロフィールイメージの書き込み失敗</li>
	 * </ul>
	 * @param celebationNo お祝い番号
	 * @param req お祝い修正パラメータ
	 * @param loginUser ログインしたユーザ情報
	 * @throws IOException プロフィールイメージの書き込み失敗
	 */
	@ResponseStatus(HttpStatus.OK)
	@PutMapping(PUT_URL)
	public void modify(@PathVariable(name = "celebrationNo", required = true) Long celebationNo, @RequestBody @Validated ModifyCelebrationAPIReqeust req,
			@AuthenticationPrincipal LoginUser loginUser)  throws IOException {
		if(Objects.isNull(loginUser)) {
			throw new AzusatoException(HttpStatus.UNAUTHORIZED, AzusatoException.I0001,
					messageSource.getMessage(AzusatoException.I0001, null, servletRequest.getLocale()));
		}

		ModifyCelebationServiceAPIRequest serviceReq = ModifyCelebationServiceAPIRequest.builder()
				.celebationNo(celebationNo)
				.name(req.getName())
				.title(req.getTitle()).content(req.getContent()).userNo(loginUser.getUSER_NO()).build();
		
		celeAPIService.modifyCelebartion(serviceReq, servletRequest.getLocale());
	}
	
	/**
	 * 「お祝い」の参照回数
	 * <ul>
	 * 	<li>200 : 参照回数更新成功</li>
	 * 	<li>400 : <br>対象データ存在なし</li>
	 * </ul>
	 * @param celebationNo お祝い番号
	 */
	@ResponseStatus(HttpStatus.OK)
	@PutMapping(READCOUNTUP_URL)
	public void readCountUp(@PathVariable(name = "celebrationNo", required = true) Long celebationNo) {
		celeAPIService.readCountUp(celebationNo, servletRequest.getLocale());
	}
	
	/**
	 * 「お祝い」と「お祝い書き込み」を論理削除する。deletedをtrueに変更
	 * <ul>
	 * 	<li>200 : 論理削除成功</li>
	 * 	<li>400 : <br>対象データ存在なし<br>生成したユーザではない場合<br>パラメータがエラー</li>
	 *  <li>401 : ログインしていない</li>
	 * </ul>
	 * @param celebationNo お祝い番号
	 * @param loginUser ログインしたユーザ情報
	 */
	@ResponseStatus(HttpStatus.OK)
	@DeleteMapping(DELETE_URL)
	public void delete(@PathVariable(name = "celebrationNo", required = true) Long celebationNo,
			@AuthenticationPrincipal LoginUser loginUser) {
		if(Objects.isNull(loginUser)) {
			throw new AzusatoException(HttpStatus.UNAUTHORIZED, AzusatoException.I0001,
					messageSource.getMessage(AzusatoException.I0001, null, servletRequest.getLocale()));
		}
		
		celeAPIService.deleteCelebartion(celebationNo, loginUser.getUSER_NO(),servletRequest.getLocale());
	}
	
//	/**
//	 * {@link NonEmtpyInputStream}を生成し、Inputstreamに読み込めるデータが存在するかチェック。
//	 * originalInputStreamは読み込み可能。
//	 * @param originalInputStream 対象のInputstream
//	 * @param locale エラーメッセージ用
//	 * @return Inputstream また読み込む可能なInputstream
//	 * @throws IOException 
//	 * @throws AzusatoException 読み込めるデータが存在しない場合
//	 */
//	private InputStream checkNonEmptyInputStream(InputStream originalInputStream, Locale locale) throws IOException {
//		try {
//			return new NonEmtpyInputStream(originalInputStream);
//		} catch (EmptyInputStreamException e) {
//			String profileImageMessage = messageSource.getMessage("profileImage", null, locale);
//			throw new AzusatoException(HttpStatus.BAD_REQUEST, AzusatoException.I0005,
//					messageSource.getMessage(AzusatoException.I0005, new String[] {profileImageMessage}, servletRequest.getLocale()));
//		}
//	}
}
