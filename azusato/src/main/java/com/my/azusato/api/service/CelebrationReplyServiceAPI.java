package com.my.azusato.api.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.my.azusato.api.controller.request.AddCelebrationReplyAPIReqeust;
import com.my.azusato.entity.CelebrationEntity;
import com.my.azusato.entity.CelebrationReplyEntity;
import com.my.azusato.entity.UserEntity;
import com.my.azusato.entity.common.CommonDateEntity;
import com.my.azusato.entity.common.CommonFlagEntity;
import com.my.azusato.entity.common.CommonUserEntity;
import com.my.azusato.entity.common.DefaultValueConstant;
import com.my.azusato.exception.AzusatoException;
import com.my.azusato.repository.CelebrationReplyRepository;
import com.my.azusato.repository.CelebrationRepository;
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
public class CelebrationReplyServiceAPI {

	private final UserRepository userRepo;

	private final MessageSource messageSource;
	
	private final CelebrationRepository celeRepo;

	private final CelebrationReplyRepository celeReplyRepo;

	/**
	 * お祝い書き込みを登録する。
	 * お祝い番号に関わる書き込みを作成したユーザ(自分除外)を重複なしで検索し、お祝い書き込み通知に登録する。
	 * @param req パラメータ
	 * @param celebationNo お祝い番号
	 * @param loginUserNo ログインしたユーザの番号
	 * @param locale エラーメッセージ用
	 * @throws ユーザ情報が存在しない。
	 */
	@Transactional
	public void addCelebartionReply(AddCelebrationReplyAPIReqeust req, Long celebationNo , Long loginUserNo , Locale locale) {
		UserEntity userEntity = userRepo.findByNoAndCommonFlagDeleteFlag(loginUserNo,ValueConstant.DEFAULT_DELETE_FLAG).orElseThrow(() -> {
			throw AzusatoException.createI0005Error(locale, messageSource, UserEntity.TABLE_NAME_KEY);
		});
		
		CelebrationEntity fetchedCelebationEntity = 
				celeRepo.findByNoAndCommonFlagDeleteFlagAndCommonUserCreateUserEntityCommonFlagDeleteFlag(celebationNo,ValueConstant.DEFAULT_DELETE_FLAG,ValueConstant.DEFAULT_DELETE_FLAG).orElseThrow(()->{
						throw AzusatoException.createI0005Error(locale, messageSource, CelebrationEntity.TABLE_NAME_KEY);
				});
		
		List<CelebrationReplyEntity> fetchedReplys = fetchedCelebationEntity.getReplys();
		Set<UserEntity> replyNoticeUsers = fetchedReplys.stream()
				.map((e)->{
					return e.getCommonUser().getCreateUserEntity();
				})
				// 自分は通知に除外する。
				.filter((e)->{
					return e.getNo() != loginUserNo;
				}).collect(Collectors.toSet());

		LocalDateTime nowLdt = LocalDateTime.now();

		CommonDateEntity commonDateEntity = userEntity.getCommonDate();
		commonDateEntity.setUpdateDatetime(nowLdt);
		
		userEntity.setName(req.getName());

		CelebrationReplyEntity insertedEntity = CelebrationReplyEntity.builder().content(req.getContent())
				.commonUser(
						CommonUserEntity.builder().createUserEntity(userEntity).updateUserEntity(userEntity).build())
				.replyNotices(replyNoticeUsers)
				.commonDate(CommonDateEntity.builder().createDatetime(nowLdt).updateDatetime(nowLdt).build())
				.commonFlag(CommonFlagEntity.builder().deleteFlag(DefaultValueConstant.DELETE_FLAG).build()).build();

		celeReplyRepo.save(insertedEntity);
	}
	
	/**
	 * 「お祝い書き込み」論理削除。
	 * お祝い番号より参照後、ない場合はエラーをスローする。
	 * ある場合は、生成したユーザか比較し、生成したユーザではない場合はエラーをスローする。
	 * 生成したユーザの場合は削除を行う。
	 * 
	 * 
	 * @param celebationReplyNo 削除対象のお祝い書き込み番号
	 * @param userNo セッションのユーザ番号
	 * @param locale エラーメッセージ用
	 * @throws AzusatoException 対象データ存在なし、生成したユーザではない場合
	 */
	@Transactional
	public void deleteCelebartionReply(Long celebationReplyNo, Long userNo,Locale locale) {
		CelebrationReplyEntity fetchedCelebationReplyEntity = 
				celeReplyRepo.findByNoAndCommonFlagDeleteFlag(celebationReplyNo,ValueConstant.DEFAULT_DELETE_FLAG).orElseThrow(()->{
						throw AzusatoException.createI0005Error(locale, messageSource, CelebrationEntity.TABLE_NAME_KEY);
				});
		
		// 生成したユーザかユーザをチェックする。
		long createdUserNo = fetchedCelebationReplyEntity.getCommonUser().getCreateUserEntity().getNo();
		
		if(createdUserNo != userNo) {
			String message = messageSource.getMessage(AzusatoException.I0006, null, locale);
			throw new AzusatoException(HttpStatus.BAD_REQUEST, AzusatoException.I0006, message);
		}
		
		LocalDateTime now = LocalDateTime.now();
		fetchedCelebationReplyEntity.getCommonFlag().setDeleteFlag(!ValueConstant.DEFAULT_DELETE_FLAG);
		fetchedCelebationReplyEntity.getCommonDate().setUpdateDatetime(now);
	
		celeReplyRepo.save(fetchedCelebationReplyEntity);
	}
	
}
