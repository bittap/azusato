package com.my.azusato.api.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.my.azusato.api.service.request.AddCelebrationServiceAPIRequest;
import com.my.azusato.api.service.request.GetCelebrationsSerivceAPIRequset;
import com.my.azusato.api.service.request.ModifyCelebationServiceAPIRequest;
import com.my.azusato.api.service.response.GetCelebrationContentSerivceAPIResponse;
import com.my.azusato.api.service.response.GetCelebrationContentSerivceAPIResponse.CelebrationReply;
import com.my.azusato.api.service.response.GetCelebrationSerivceAPIResponse;
import com.my.azusato.api.service.response.GetCelebrationsSerivceAPIResponse;
import com.my.azusato.api.service.response.GetCelebrationsSerivceAPIResponse.Celebration;
import com.my.azusato.entity.CelebrationContentEntity;
import com.my.azusato.entity.CelebrationSummaryEntity;
import com.my.azusato.entity.ProfileEntity;
import com.my.azusato.entity.UserEntity;
import com.my.azusato.entity.UserEntity.Type;
import com.my.azusato.entity.common.CommonDateEntity;
import com.my.azusato.entity.common.CommonFlagEntity;
import com.my.azusato.entity.common.CommonUserEntity;
import com.my.azusato.entity.common.DefaultValueConstant;
import com.my.azusato.exception.AzusatoException;
import com.my.azusato.page.MyPageRequest;
import com.my.azusato.page.MyPaging;
import com.my.azusato.repository.CelebrationContentRepository;
import com.my.azusato.repository.CelebrationSummaryRepository;
import com.my.azusato.repository.UserRepository;
import com.my.azusato.view.controller.common.ValueConstant;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Service API for celebration.
 * <ul>
 * <li>add a celebration.</li>
 * </ul>
 * 
 * @author kim-t
 *
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CelebrationServiceAPI {

	private final UserRepository userRepo;

	private final MessageSource messageSource;

	private final CelebrationSummaryRepository celeSummaryRepo;
	
	private final CelebrationContentRepository celeContentRepo;

	/**
	 * delegate
	 * {@link CelebrationServiceAPI#addCelebartion(AddCelebrationServiceAPIRequest, Locale, String)}.
	 * 
	 * @param req    request parameter.
	 * @param locale locale of client.
	 */
	public void addCelebartionAdmin(AddCelebrationServiceAPIRequest req, Locale locale) {
		addCelebartion(req, locale, "admin");
	}

	/**
	 * delegate
	 * {@link CelebrationServiceAPI#addCelebartion(AddCelebrationServiceAPIRequest, Locale, String)}.
	 * 
	 * @param req    request parameter.
	 * @param locale locale of client.
	 */
	public void addCelebartion(AddCelebrationServiceAPIRequest req, Locale locale) {
		addCelebartion(req, locale, "not_admin");
	}

	/**
	 * add a celebration for celebration table. if userType is not admin. add notice
	 * data for noticing to admin.
	 * 
	 * 
	 * @param req      request parameter.
	 * @param locale   locale of client. for error message
	 * @param userType admin,etc
	 */
	@Transactional
	private void addCelebartion(AddCelebrationServiceAPIRequest req, Locale locale, String userType) {
		log.debug("{}#addCelebartion , req : {}, locale : {}", CelebrationServiceAPI.class.getName(), req, locale);

		UserEntity userEntity = userRepo.findByNoAndCommonFlagDeleteFlag(req.getUserNo(),ValueConstant.DEFAULT_DELETE_FLAG).orElseThrow(() -> {
			throw AzusatoException.createI0005Error(locale, messageSource, UserEntity.TABLE_NAME_KEY);
		});

		Set<UserEntity> admins = null;

		if (userType.equals("not_admin")) {
			// for create celebration_notice table
			// Select that user type is admin.
			admins = userRepo.findByUserTypeAndCommonFlagDeleteFlag(Type.admin.toString(),ValueConstant.DEFAULT_DELETE_FLAG);
		}
		
		LocalDateTime nowLdt = LocalDateTime.now();
		
		ProfileEntity profileEntity = userEntity.getProfile();
		profileEntity.setImageBase64(req.getProfileImageBase64());
		profileEntity.setImageType(req.getProfileImageType());

		CommonDateEntity commonDateEntity = userEntity.getCommonDate();
		commonDateEntity.setUpdateDatetime(nowLdt);
		
		userEntity.setName(req.getName());
		
		CelebrationContentEntity insertedEntity = CelebrationContentEntity.builder().title(req.getTitle()).content(req.getContent())
				.commonUser(
						CommonUserEntity.builder().createUserEntity(userEntity).updateUserEntity(userEntity).build())
				.notices(admins).readCount(DefaultValueConstant.READ_COUNT)
				.commonDate(CommonDateEntity.builder().createDatetime(nowLdt).updateDatetime(nowLdt).build())
				.commonFlag(CommonFlagEntity.builder().deleteFlag(DefaultValueConstant.DELETE_FLAG).build()).build();

		celeContentRepo.save(insertedEntity);

		log.debug("{}#addCelebartion END");
	}
	
	/**
	 * お祝い修正。
	 * お祝い番号より参照後、ない場合はエラーをスローする。
	 * ある場合は、生成したユーザか比較し、生成したユーザではない場合はエラーをスローする。
	 * 生成したユーザの場合は更新を行う。
	 * 
	 * 
	 * @param ModifyCelebationServiceAPIRequest 検索条件
	 * @param locale エラーメッセージ用
	 * @throws AzusatoException 対象データ存在なし、生成したユーザではない場合
	 */
	@Transactional
	public void modifyCelebartion(ModifyCelebationServiceAPIRequest req , Locale locale) {
		CelebrationContentEntity fetchedCelebationEntity = 
				celeContentRepo.findByNoAndCommonFlagDeleteFlagAndCommonUserCreateUserEntityCommonFlagDeleteFlag(req.getCelebationNo(),ValueConstant.DEFAULT_DELETE_FLAG,ValueConstant.DEFAULT_DELETE_FLAG).orElseThrow(()->{
						throw AzusatoException.createI0005Error(locale, messageSource, CelebrationContentEntity.TABLE_NAME_KEY);
				});
		
		// 生成したユーザかユーザをチェックする。
		long createdUserNo = fetchedCelebationEntity.getCommonUser().getCreateUserEntity().getNo();
		
		if(createdUserNo != req.getUserNo()) {
			String message = messageSource.getMessage(AzusatoException.I0006, null, locale);
			throw new AzusatoException(HttpStatus.BAD_REQUEST, AzusatoException.I0006, message);
		}
		LocalDateTime now = LocalDateTime.now();
		fetchedCelebationEntity.setTitle(req.getTitle());
		fetchedCelebationEntity.setContent(req.getContent());
		fetchedCelebationEntity.getCommonUser().getCreateUserEntity().setName(req.getName());
		fetchedCelebationEntity.getCommonUser().getCreateUserEntity().getProfile().setImageType(req.getProfileImageType());
		fetchedCelebationEntity.getCommonUser().getCreateUserEntity().getProfile().setImageBase64(req.getProfileImageBase64());
		fetchedCelebationEntity.getCommonDate().setUpdateDatetime(now);
		fetchedCelebationEntity.getCommonUser().getCreateUserEntity().getCommonDate().setUpdateDatetime(now);
	
		celeContentRepo.save(fetchedCelebationEntity);
	}
	
	/**
	 * 「お祝い」と「お祝い書き込み」論理削除。
	 * お祝い番号より参照後、ない場合はエラーをスローする。
	 * ある場合は、生成したユーザか比較し、生成したユーザではない場合はエラーをスローする。
	 * 生成したユーザの場合は削除を行う。
	 * 
	 * 
	 * @param celebationNo 削除対象のお祝い番号
	 * @param userNo セッションのユーザ番号
	 * @param locale エラーメッセージ用
	 * @throws AzusatoException 対象データ存在なし、生成したユーザではない場合
	 */
	@Transactional
	public void deleteCelebartion(Long celebationNo, Long userNo,Locale locale) {
		CelebrationSummaryEntity fetchedCelebationEntity = 
				// ユーザが削除されたとしても、削除処理は行われるように
				celeSummaryRepo.findByNoAndCommonFlagDeleteFlag(celebationNo,ValueConstant.DEFAULT_DELETE_FLAG).orElseThrow(()->{
						throw AzusatoException.createI0005Error(locale, messageSource, CelebrationContentEntity.TABLE_NAME_KEY);
				});
		
		// 生成したユーザかユーザをチェックする。
		long createdUserNo = fetchedCelebationEntity.getCommonUser().getCreateUserEntity().getNo();
		
		if(createdUserNo != userNo) {
			String message = messageSource.getMessage(AzusatoException.I0006, null, locale);
			throw new AzusatoException(HttpStatus.BAD_REQUEST, AzusatoException.I0006, message);
		}
		
		LocalDateTime now = LocalDateTime.now();
		fetchedCelebationEntity.getCommonFlag().setDeleteFlag(!ValueConstant.DEFAULT_DELETE_FLAG);
		fetchedCelebationEntity.getCommonDate().setUpdateDatetime(now);
		
		// 書き込み論理削除
		fetchedCelebationEntity.getReplys().parallelStream().forEach((e)->{
			e.getCommonDate().setUpdateDatetime(now);
			e.getCommonFlag().setDeleteFlag(!ValueConstant.DEFAULT_DELETE_FLAG);	
		});
	
		celeSummaryRepo.save(fetchedCelebationEntity);
	}
	

	/**
	 * 「お祝い」の参照回数をアップする。
	 * お祝い番号より参照後、ない場合はエラーをスローする。
	 * 
	 * @param celebationNo 削除対象のお祝い番号
	 * @param locale エラーメッセージ用
	 * @throws AzusatoException 対象データ存在なし
	 */
	@Transactional
	public void readCountUp(Long celebationNo, Locale locale) {
		CelebrationSummaryEntity fetchedCelebationEntity = 
				// note：ユーザが開いた状態でのコンテンツはクリック可能にするため、deletedは使わない。
				celeSummaryRepo.findById(celebationNo).orElseThrow(()->{
						throw AzusatoException.createI0005Error(locale, messageSource, CelebrationContentEntity.TABLE_NAME_KEY);
				});
		
		int upedReadCount = fetchedCelebationEntity.getReadCount() + 1;
		fetchedCelebationEntity.setReadCount(upedReadCount);
	
		celeSummaryRepo.save(fetchedCelebationEntity);
	}

	/**
	 * 対象のお祝いを返却する。
	 * @param celebationNo 検索条件
	 * @param locale エラーメッセージ用
	 * @return 対象のお祝い情報
	 * @throws AzusatoException 対象データが存在しない場合
	 */
	public GetCelebrationSerivceAPIResponse getCelebration(Long celebationNo , Locale locale) {
		CelebrationContentEntity fetchedCelebationEntity = celeContentRepo.findById(celebationNo).orElseThrow(()->{
			throw AzusatoException.createI0005Error(locale, messageSource, CelebrationContentEntity.TABLE_NAME_KEY);
		});
		
		return GetCelebrationSerivceAPIResponse.builder()
					.celebrationNo(fetchedCelebationEntity.getNo())
					.title(fetchedCelebationEntity.getTitle())
					.content(fetchedCelebationEntity.getContent())
					.name(fetchedCelebationEntity.getCommonUser().getCreateUserEntity().getName())
					.profileImageType(fetchedCelebationEntity.getCommonUser().getCreateUserEntity().getProfile().getImageType())
					.profileImageBase64(fetchedCelebationEntity.getCommonUser().getCreateUserEntity().getProfile().getImageBase64())
					.build();
	}
	
	/**
	 * 対象のお祝いを返却する。
	 * @param celebationNo 検索条件
	 * @param userNo ログインしたユーザ番号。修正、削除権限があるかどうかチェックするため
	 * @param locale エラーメッセージ用
	 * @return 対象のお祝い情報
	 * @throws AzusatoException テーブルにお祝いデータが存在しない。
	 */
	@Transactional
	public GetCelebrationContentSerivceAPIResponse getCelebrationContent(Long celebationNo , Long userNo, Locale locale) {
		CelebrationContentEntity fetchedCelebationEntity = celeContentRepo.findById(celebationNo).orElseThrow(()->{
			throw AzusatoException.createI0005Error(locale, messageSource, CelebrationContentEntity.TABLE_NAME_KEY);
		});
		
		return GetCelebrationContentSerivceAPIResponse.builder()
			.content(fetchedCelebationEntity.getContent())
			.no(fetchedCelebationEntity.getNo())
			.owner(fetchedCelebationEntity.getCommonUser().getCreateUserEntity().getNo() == userNo ? true : false)
			.replys(fetchedCelebationEntity.getReplys().stream()
					// deleted == falseだけ
					.filter((e1)->{
						return e1.getCommonFlag().getDeleteFlag() == ValueConstant.DEFAULT_DELETE_FLAG;
					})
					// No昇順Orderby
					.sorted((e1,e2)->Long.compare(e1.getNo(), e2.getNo()))
					.map((e2)->{
						CelebrationReply reply = CelebrationReply.builder()
								.no(e2.getNo())
								.content(e2.getContent())
								.createdDatetime(e2.getCommonDate().getCreateDatetime())
								.owner(e2.getCommonUser().getCreateUserEntity().getNo() == userNo ? true : false)
								.name(e2.getCommonUser().getCreateUserEntity().getName())
								.profileImageType(e2.getCommonUser().getCreateUserEntity().getProfile().getImageType())
								.profileImageBase64(e2.getCommonUser().getCreateUserEntity().getProfile().getImageBase64())
								.build();
						return reply;
					})
					.collect(Collectors.toList())
			)
			.build();
	}

	/**
	 * お祝いリストを返却する。
	 * お祝いNoの降順、書き込みNoの昇順で取得
	 * @param req お祝いリストに関するリクエスト
	 * @return お祝い+お祝い書き込みリスト
	 */
	public GetCelebrationsSerivceAPIResponse getCelebrations(GetCelebrationsSerivceAPIRequset req) {
		// 注意 : 一番最初のパラメータpageは0から始まる。
		Pageable sortedByNo = PageRequest.of(req.getPageReq().getCurrentPageNo()-1, req.getPageReq().getPageOfElement(),Sort.by(Direction.DESC,"no"));
		Page<CelebrationSummaryEntity> celebrationEntitys = celeSummaryRepo
				.findAllByCommonFlagDeleteFlagAndCommonUserCreateUserEntityCommonFlagDeleteFlag(sortedByNo,ValueConstant.DEFAULT_DELETE_FLAG,ValueConstant.DEFAULT_DELETE_FLAG);
		
		GetCelebrationsSerivceAPIResponse response = new GetCelebrationsSerivceAPIResponse();
		
		// ページング
		response.setPage(MyPaging.of(
					MyPageRequest.of(req.getPageReq(), celebrationEntitys.getTotalElements())
				));
		
		List<Celebration> celebrations = celebrationEntitys.stream().map((e)->{
			return Celebration.builder()
					.title(e.getTitle())
					.name(e.getCommonUser().getCreateUserEntity().getName())
					.profileImageType(e.getCommonUser().getCreateUserEntity().getProfile().getImageType())
					.profileImageBase64(e.getCommonUser().getCreateUserEntity().getProfile().getImageBase64())
					.no(e.getNo())
					.createdDatetime(e.getCommonDate().getCreateDatetime())
					.build();
		}).collect(Collectors.toList());
		
		response.setCelebrations(celebrations);
		
		
		return response;
	}
}
