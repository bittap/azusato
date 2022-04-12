package com.my.azusato.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.my.azusato.entity.CelebrationEntity;

@Repository
public interface CelebrationRepository extends JpaRepository<CelebrationEntity, Long> {

}
