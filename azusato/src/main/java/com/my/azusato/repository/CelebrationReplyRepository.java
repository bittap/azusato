package com.my.azusato.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.my.azusato.entity.CelebrationReplyEntity;

@Repository
public interface CelebrationReplyRepository extends JpaRepository<CelebrationReplyEntity, Long> {

	/**
	 * where no = "celebraionReplyNo" and delete_flag = "deleted"
	 * @param celebraionNo お祝い書き込み番号
	 * @param deleted 削除フラグ。基本falseで検索
	 * @return お祝い書き込み
	 */
	public Optional<CelebrationReplyEntity> findByNoAndCommonFlagDeleteFlag(Long celebraionReplyNo, boolean deleted);
}
