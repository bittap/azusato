package com.my.azusato.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.my.azusato.entity.WeddingAttender;

@Repository
public interface WeddingAttenderRepository extends JpaRepository<WeddingAttender, Long> {

}
