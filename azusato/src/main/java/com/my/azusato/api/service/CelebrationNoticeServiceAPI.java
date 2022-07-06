package com.my.azusato.api.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

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
import com.my.azusato.login.LoginUser;
import com.my.azusato.repository.CelebrationNoticeRepository;
import com.my.azusato.view.controller.common.ValueConstant;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class CelebrationNoticeServiceAPI {
	
	private final CelebrationNoticeRepository celeNotiRepo;

	/**
	 * お祝いリストを返却する。
	 * お祝いNoの降順、書き込みNoの昇順で取得
	 * @param req お祝いリストに関するリクエスト
	 * @return お祝い+お祝い書き込みリスト
	 */
	@MethodAnnotation(description = "お祝い通知情報リストの返却")
	public List<GetCelebrationNoticesSerivceAPIResponse> celebrationNotices(GetCelebrationsSerivceAPIRequset req) {
		Page<CelebrationNoticeEntity> celebrationNotices = getCelebrationNotices(req.getPageReq().getCurrentPageNo()-1, req.getPageReq().getPageOfElement());
		
		
		return celebrationNotices.stream().map((e)->{
			String title;
			LocalDateTime createdDatetime;
			if(Objects.nonNull(e.getReply())) {
				title = e.getReply().getContent();
				createdDatetime = e.getReply().getCommonDate().getCreateDatetime();
			}else {
				title = e.getCelebration().getTitle();
				createdDatetime = e.getCelebration().getCommonDate().getCreateDatetime();
			}
			return GetCelebrationNoticesSerivceAPIResponse.builder()
					.name(e.getCelebration().getCommonUser().getCreateUserEntity().getName())
					.title(title)
					.createdDatetime(createdDatetime)
					.celebrationNo(e.getCelebration().getNo())
					.celebrationReplyNo(e.getReply().getNo())
					.build();
		}).collect(Collectors.toList());
	}
	
	private Page<CelebrationNoticeEntity> getCelebrationNotices(Integer currentPageNo, Integer pageOfElement ){
		// 注意 : 一番最初のパラメータpageは0から始まる。
		// TODO 정렬 : read 안 읽은거, 축하 내림차순 ,축하댓글 내림차순
		List<Order> orders = List.of(Order.asc("readed"),Order.desc("no"));
		Pageable sortedByNo = PageRequest.of(currentPageNo, pageOfElement,Sort.by(orders));
		return celeNotiRepo.findAllByCommonFlagDelete(sortedByNo, ValueConstant.DEFAULT_DELETE_FLAG);
	}

	@Transactional
	@MethodAnnotation(description = "お祝い通知の既読処理")
	public void read(Long celebationNo, LoginUser loginUser, Locale locale) {
		List<CelebrationNoticeEntity> notices = celeNotiRepo.findAllByCelebrationNoAndTargetUserNo(celebationNo, celebationNo);
		
		for (CelebrationNoticeEntity notice : notices) {
			notice.setReaded(true);
			notice.setReadDatetime(LocalDateTime.now());
			
			celeNotiRepo.save(notice);
		}
	}

}
