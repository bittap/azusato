package com.my.azusato.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.my.azusato.entity.CelebrationNoticeEntity;
import com.my.azusato.entity.CelebrationNoticePk;

public interface CelebrationNoticeRepository extends JpaRepository<CelebrationNoticeEntity, CelebrationNoticePk> {

	public List<CelebrationNoticeEntity> findAllByUserNo(long userNo);
}
