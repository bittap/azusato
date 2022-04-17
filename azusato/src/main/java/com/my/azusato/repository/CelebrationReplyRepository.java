package com.my.azusato.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.my.azusato.entity.CelebrationReplyEntity;

@Repository
public interface CelebrationReplyRepository extends JpaRepository<CelebrationReplyEntity, Long> {

}
