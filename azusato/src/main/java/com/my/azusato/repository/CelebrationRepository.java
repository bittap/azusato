package com.my.azusato.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.my.azusato.entity.CelebrationEntity;

@Repository
public interface CelebrationRepository extends PagingAndSortingRepository<CelebrationEntity, Long>, JpaRepository<CelebrationEntity, Long> {

	//public List<CelebrationEntity> findAllByNoticesNo(long userNo);
	
	public List<CelebrationEntity> findAllByNoIn(List<Long> nos);
}
