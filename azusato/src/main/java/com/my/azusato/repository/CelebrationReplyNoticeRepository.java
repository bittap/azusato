package com.my.azusato.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.my.azusato.entity.CelebrationReplyNoticeEntity;
import com.my.azusato.entity.CelebrationReplyNoticePk;

public interface CelebrationReplyNoticeRepository extends JpaRepository<CelebrationReplyNoticeEntity, CelebrationReplyNoticePk> {

	public List<CelebrationReplyNoticeEntity> findAllByUserNo(long userNo);
}
