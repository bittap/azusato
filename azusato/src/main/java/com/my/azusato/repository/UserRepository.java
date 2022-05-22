package com.my.azusato.repository;

import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.my.azusato.entity.UserEntity;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
	
	public Set<UserEntity> findByUserTypeAndCommonFlagDeleteFlag(String userType,boolean deleted);
	
	/**
	 * where no = "userNo" and delete_flag = "deleted"
	 * @param userNo ユーザ情報番号
	 * @param deleted 削除フラグ。基本falseで検索
	 * @return ユーザ情報
	 */
	public Optional<UserEntity> findByNoAndCommonFlagDeleteFlag(long userNo,boolean deleted);

}
