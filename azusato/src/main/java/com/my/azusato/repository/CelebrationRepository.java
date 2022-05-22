package com.my.azusato.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.my.azusato.entity.CelebrationEntity;

@Repository
public interface CelebrationRepository extends PagingAndSortingRepository<CelebrationEntity, Long>, JpaRepository<CelebrationEntity, Long> {

	//public List<CelebrationEntity> findAllByNoticesNo(long userNo);
	
	public List<CelebrationEntity> findAllByNoIn(List<Long> nos);
	
	/**
	 * where no = "celebationNo" and delete_flag = "deleted" and create_user.delete_flag = "deleted"
	 * @param celebraionNo お祝い番号
	 * @param deleted 削除フラグ。基本falseで検索
	 * @param createUserDeleted 生成したユーザの削除フラグ
	 * @return お祝い
	 */
	public Optional<CelebrationEntity> findByNoAndCommonFlagDeleteFlagAndCommonUserCreateUserEntityCommonFlagDeleteFlag(Long celebraionNo, boolean deleted, boolean createUserDeleted);
	
	
	/**
	 * where delete_flag = "" and create_user.delete_flag = "deleted" order by xx limit xx 
	 * @param pageable ページングとソート情報
	 * @param deleted 削除フラグ。基本falseで検索
	 * @param createUserDeleted 生成したユーザの削除フラグ
	 * @return お祝いリスト
	 */
	public Page<CelebrationEntity> findAllByCommonFlagDeleteFlagAndCommonUserCreateUserEntityCommonFlagDeleteFlag(Pageable pageable, boolean deleted , boolean createUserDeleted);
}
