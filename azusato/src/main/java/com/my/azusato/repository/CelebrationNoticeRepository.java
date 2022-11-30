package com.my.azusato.repository;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import com.my.azusato.entity.CelebrationNoticeEntity;

@Repository
public interface CelebrationNoticeRepository
    extends PagingAndSortingRepository<CelebrationNoticeEntity, Long>,
    JpaRepository<CelebrationNoticeEntity, Long> {

  /**
   * where target_user_no = "" and delete_flag = "" order by xx limit xx
   * 
   * @param pageable ページングとソート情報
   * @param deleted 削除フラグ。基本falseで検索
   * @param createUserDeleted 生成したユーザの削除フラグ
   * @return お祝通知いリスト
   */
  public Page<CelebrationNoticeEntity> findAllByTargetUserNoAndCommonFlagDeleteFlag(
      Pageable pageable, Long targetUserNo, boolean deleted);

  /**
   * where celebration_no = "" and target_user_no = ""
   * 
   * @param celebraionNo お祝いバン後
   * @param targetUserNo ターゲットユーザ情報
   * @return お祝通知いリスト
   */
  public List<CelebrationNoticeEntity> findAllByCelebrationNoAndTargetUserNo(Long celebraionNo,
      Long targetUserNo);

  /**
   * where celebration_no = ""
   * 
   * @param celebraionNo お祝いバン後
   * @return お祝通知いリスト
   */
  public List<CelebrationNoticeEntity> findAllByCelebrationNo(Long celebraionNo);
}
