package com.my.azusato.api.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import javax.transaction.Transactional;

import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.stereotype.Service;

import com.my.azusato.annotation.MethodAnnotation;
import com.my.azusato.api.service.request.GetCelebrationsSerivceAPIRequset;
import com.my.azusato.api.service.response.GetCelebrationNoticesSerivceAPIResponse;
import com.my.azusato.entity.CelebrationNoticeEntity;
import com.my.azusato.exception.AzusatoException;
import com.my.azusato.repository.CelebrationNoticeRepository;
import com.my.azusato.view.controller.common.ValueConstant;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class CelebrationNoticeServiceAPI {
	
	private final CelebrationNoticeRepository celeNotiRepo;
	
	private final MessageSource messageSource;

	/**
	 * お祝いリストを返却する。
	 * お祝いNoの降順、書き込みNoの昇順で取得
	 * @param req お祝いリストに関するリクエスト
	 * @param loginUserNo ログインしたユーザ番号
	 * @return お祝い通知情報リスト
	 */
	@MethodAnnotation(description = "お祝い通知情報リストの返却")
	@Transactional
	public GetCelebrationNoticesSerivceAPIResponse celebrationNotices(GetCelebrationsSerivceAPIRequset req, Long loginUserNo) {
		Page<CelebrationNoticeEntity> celebrationNotices = getCelebrationNotices(req.getPageReq().getCurrentPageNo()-1, req.getPageReq().getPageOfElement(), loginUserNo);
		
		
		GetCelebrationNoticesSerivceAPIResponse response = new GetCelebrationNoticesSerivceAPIResponse();
		List<GetCelebrationNoticesSerivceAPIResponse.Notice> notices = new ArrayList<>();
		
		int noReadLength = 0;
		for (CelebrationNoticeEntity celebrationNotice : celebrationNotices) {
			if(celebrationNotice.getReaded() == false) {
				noReadLength++;
			}
			String title;
			LocalDateTime createdDatetime;
			Long celeReplyNo = null;
			String profileImagePath;
			if(Objects.nonNull(celebrationNotice.getReply())) {
				title = celebrationNotice.getReply().getContent();
				createdDatetime = celebrationNotice.getReply().getCommonDate().getCreateDatetime();
				celeReplyNo = celebrationNotice.getReply().getNo();
				profileImagePath =celebrationNotice.getReply().getCommonUser().getCreateUserEntity().getProfile().getImagePath();
			}else {
				title = celebrationNotice.getCelebration().getTitle();
				createdDatetime = celebrationNotice.getCelebration().getCommonDate().getCreateDatetime();
				profileImagePath = celebrationNotice.getCelebration().getCommonUser().getCreateUserEntity().getProfile().getImagePath();
			}
			GetCelebrationNoticesSerivceAPIResponse.Notice notice = GetCelebrationNoticesSerivceAPIResponse.Notice.builder()
					.name(celebrationNotice.getCelebration().getCommonUser().getCreateUserEntity().getName())
					.title(title)
					.profileImagePath(profileImagePath)
					.createdDatetime(createdDatetime)
					.celebrationNo(celebrationNotice.getCelebration().getNo())
					.celebrationReplyNo(celeReplyNo)
					.readed(celebrationNotice.getReaded())
					.build();
			
			notices.add(notice);
		}
		
		response.setNotices(notices);
		response.setNoReadLength(noReadLength);
		
		return response;
	}
	
	private Page<CelebrationNoticeEntity> getCelebrationNotices(Integer currentPageNo, Integer pageOfElement ,Long loginUserNo){
		// 注意 : 一番最初のパラメータpageは0から始まる。
		List<Order> orders = List.of(Order.asc("readed"),Order.desc("celebration_no"),Order.desc("reply.no"));
		Pageable sortedByNo = PageRequest.of(currentPageNo, pageOfElement,Sort.by(orders));
		return celeNotiRepo.findAllByTargetUserNoAndCommonFlagDeleteFlag(sortedByNo, loginUserNo , ValueConstant.DEFAULT_DELETE_FLAG);
	}

	/**
	 * お祝い通知の"既読フラグ"をtrueに変更する。"celebationNo"と"loginUserNo"に一致する通知リストを返却し、"既読フラグ"をtrueに変更する。
	 * "celebationNo"と"loginUserNo"に一致する通知リストが一つも存在しない場合は、400エラーをスローする。
	 * @param celebationNo　お祝い番号
	 * @param loginUserNo ログインしたユーザの情報
	 * @param locale エラーメッセージ用
	 */
	@Transactional
	@MethodAnnotation(description = "お祝い通知の既読処理")
	public void read(Long celebationNo, long loginUserNo, Locale locale) {
		List<CelebrationNoticeEntity> notices = celeNotiRepo.findAllByCelebrationNoAndTargetUserNo(celebationNo, loginUserNo);
		
		if(notices.size() == 0)
			throw AzusatoException.createI0005Error(locale, messageSource, CelebrationNoticeEntity.TABLE_NAME_KEY);
		
		LocalDateTime now = LocalDateTime.now();
		for (CelebrationNoticeEntity notice : notices) {
			notice.setReaded(true);
			notice.setReadDatetime(now);
			notice.getCommonDate().setUpdateDatetime(now);
			
			celeNotiRepo.save(notice);
		}
	}

}
