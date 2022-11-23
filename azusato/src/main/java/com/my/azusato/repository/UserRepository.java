package com.my.azusato.repository;

import java.util.Optional;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.my.azusato.entity.UserEntity;
import com.my.azusato.entity.UserEntity.Type;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

  /**
   * where id = "userId"
   * 
   * @param userId ユーザID
   * @return ユーザ情報
   */
  public Optional<UserEntity> findById(String userId);

  /**
   * タイプによるユーザ情報取得 where serType = "userType" and delete_flag = "deleted"
   * 
   * @param userType {@link Type}
   * @param deleted 削除フラグ。基本falseで検索
   * @return タイプによるユーザ情報Set
   */
  public Set<UserEntity> findByUserTypeAndCommonFlagDeleteFlag(String userType, boolean deleted);

  /**
   * where no = "userNo" and delete_flag = "deleted"
   * 
   * @param userNo ユーザ情報番号
   * @param deleted 削除フラグ。基本falseで検索
   * @return ユーザ情報
   */
  public Optional<UserEntity> findByNoAndCommonFlagDeleteFlag(long userNo, boolean deleted);

  /**
   * where id = "userId" and delete_flag = "deleted"
   * 
   * @param userId ユーザID
   * @param deleted 削除フラグ。基本falseで検索
   * @return ユーザ情報
   */
  public Optional<UserEntity> findByIdAndCommonFlagDeleteFlag(String userId, boolean deleted);

  /**
   * where id = "userId" and userType = "userType" and delete_flag = "deleted"
   * 
   * @param userId ユーザID
   * @param userType {@link Type}
   * @param deleted 削除フラグ。基本falseで検索
   * @return ユーザ情報
   */
  public Optional<UserEntity> findByIdAndUserTypeAndCommonFlagDeleteFlag(String userId,
      String userType, boolean deleted);

}
