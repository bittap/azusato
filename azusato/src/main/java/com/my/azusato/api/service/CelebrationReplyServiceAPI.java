package com.my.azusato.api.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.my.azusato.annotation.MethodAnnotation;
import com.my.azusato.api.controller.request.AddCelebrationReplyAPIReqeust;
import com.my.azusato.entity.CelebrationEntity;
import com.my.azusato.entity.CelebrationNoticeEntity;
import com.my.azusato.entity.CelebrationReplyEntity;
import com.my.azusato.entity.UserEntity;
import com.my.azusato.entity.common.CommonDateEntity;
import com.my.azusato.entity.common.CommonFlagEntity;
import com.my.azusato.entity.common.CommonUserEntity;
import com.my.azusato.entity.common.DefaultValueConstant;
import com.my.azusato.exception.AzusatoException;
import com.my.azusato.repository.CelebrationNoticeRepository;
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
	
	private final CelebrationNoticeRepository celeNotiRepo;

	/**
	 * 「お祝い書き込み」と「お祝い書き込み通知」を登録する。
	 * 通知追加処理方法
	 * <pre>
	 * 	お祝い作成者 + 書き込み作成者達 + 重複除外 {@code -} 本人除外
	 * </pre>
	 * @param req パラメータ
	 * @param celebationNo お祝い番号
	 * @param loginUserNo ログインしたユーザの番号
	 * @param locale エラーメッセージ用
	 * @throws ユーザ情報、お祝い情報が存在しない。
	 */
	@Transactional
	@MethodAnnotation(description = "お祝い書き込み情報を登録する")
	public void addCelebartionReply(AddCelebrationReplyAPIReqeust req, Long celebationNo , Long loginUserNo , Locale locale) {
		UserEntity loginUserEntity = userRepo.findByNoAndCommonFlagDeleteFlag(loginUserNo,ValueConstant.DEFAULT_DELETE_FLAG).orElseThrow(() -> {
			throw AzusatoException.createI0005Error(locale, messageSource, UserEntity.TABLE_NAME_KEY);
		});
		
		CelebrationEntity fetchedCelebrationEntity = 
				celeRepo.findByNoAndCommonFlagDeleteFlagAndCommonUserCreateUserEntityCommonFlagDeleteFlag(celebationNo,ValueConstant.DEFAULT_DELETE_FLAG,ValueConstant.DEFAULT_DELETE_FLAG).orElseThrow(()->{
						throw AzusatoException.createI0005Error(locale, messageSource, CelebrationEntity.TABLE_NAME_KEY);
				});
		
		
		LocalDateTime nowLdt = LocalDateTime.now();

		CommonDateEntity commonDateEntity = loginUserEntity.getCommonDate();
		commonDateEntity.setUpdateDatetime(nowLdt);
		
		loginUserEntity.setName(req.getName());

		CelebrationReplyEntity insertedEntity = CelebrationReplyEntity.builder().content(req.getContent())
				.celebrationNo(celebationNo)
				.commonUser(
						CommonUserEntity.builder().createUserEntity(loginUserEntity).updateUserEntity(loginUserEntity).build())
				//.replyNotices(replyNoticeUsers)
				.commonDate(CommonDateEntity.builder().createDatetime(nowLdt).updateDatetime(nowLdt).build())
				.commonFlag(CommonFlagEntity.builder().deleteFlag(DefaultValueConstant.DELETE_FLAG).build()).build();

		// TODO お祝いテーブルから通知挿入できるように修正
		// 通知追加
		//insertedEntity.setNotices(notices);
		
		celeReplyRepo.save(insertedEntity);
		
		List<CelebrationNoticeEntity> notices = new ArrayList<>();
		
		List<CelebrationReplyEntity> fetchedReplys = fetchedCelebrationEntity.getReplys();
		// 書き込み作成者達
		Set<UserEntity> fetchedReplyUsers = fetchedReplys.stream()
				.map((e)-> e.getCommonUser().getCreateUserEntity())
				.collect(Collectors.toSet());
		// 通知対象者
		Set<UserEntity> replyNoticeUsers = getNoitceTragetUsers(fetchedReplyUsers, loginUserEntity, fetchedCelebrationEntity.getCommonUser().getCreateUserEntity());

		
		for (UserEntity replyNoticeUser : replyNoticeUsers) {
			CelebrationNoticeEntity notice = CelebrationNoticeEntity.builder()
												.celebration(fetchedCelebrationEntity)
												.reply(insertedEntity)
												.readed(false)
												.targetUser(replyNoticeUser)
												.commonDate(CommonDateEntity.builder().createDatetime(nowLdt).updateDatetime(nowLdt).build())
												.commonFlag(CommonFlagEntity.builder().deleteFlag(DefaultValueConstant.DELETE_FLAG).build()).build();
			
			notices.add(notice);
			celeNotiRepo.save(notice);
		}
	}
	
	/**
	 * 	通知対象者を探す方法
	 * <pre>
	 * 	お祝い作成者 + 書き込み作成者達 + 重複除外 {@code -} 本人除外
	 * </pre>
	 * @param fetchedReplyUsers 書き込み作成者達
	 * @param loginUser 本人
	 * @param celebrationUser お祝い作成者
	 * @return 通知対象者
	 */
	private Set<UserEntity> getNoitceTragetUsers(Set<UserEntity> fetchedReplyUsers, UserEntity loginUser , UserEntity celebrationUser){
		log.debug("通知対象者取得 書き込み作成者達 : {}, 本人 : {}, お祝い作成者 : {} ",
				fetchedReplyUsers.stream().map(UserEntity::getNo).collect(Collectors.toList()),loginUser.getNo(),celebrationUser.getNo());
		// 重複除外
		Set<UserEntity> replyNoticeUsers = new HashSet<>();
		
		// お祝い作成者
		replyNoticeUsers.add(celebrationUser);
		// 書き込み作成者達
		for (UserEntity fetchedReplyUser : fetchedReplyUsers) {
			replyNoticeUsers.add(fetchedReplyUser);
		}
		// 本人除外
		replyNoticeUsers.remove(loginUser);
		log.debug("通知対象者取得 結果 : {} ",replyNoticeUsers.stream().map(UserEntity::getNo).collect(Collectors.toList()));
		return replyNoticeUsers;
	}
	
	/**
	 * 「お祝い書き込み」と「お祝い通知」論理削除。
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
	@MethodAnnotation(description = "お祝い書き込み情報を論理削除する")
	public void deleteCelebartionReply(Long celebationReplyNo, Long userNo,Locale locale) {
		CelebrationReplyEntity fetchedCelebationReplyEntity = 
				celeReplyRepo.findByNoAndCommonFlagDeleteFlag(celebationReplyNo,ValueConstant.DEFAULT_DELETE_FLAG).orElseThrow(()->{
						throw AzusatoException.createI0005Error(locale, messageSource, CelebrationReplyEntity.TABLE_NAME_KEY);
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
		
		// お祝い通知論理削除
		fetchedCelebationReplyEntity.getNotices().parallelStream().forEach((e)->{
			e.getCommonDate().setUpdateDatetime(now);
			e.getCommonFlag().setDeleteFlag(!ValueConstant.DEFAULT_DELETE_FLAG);		
		});
	
		celeReplyRepo.save(fetchedCelebationReplyEntity);
	}
	
}
