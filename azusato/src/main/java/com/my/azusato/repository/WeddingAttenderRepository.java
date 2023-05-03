package com.my.azusato.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.my.azusato.entity.WeddingAttender;

@Repository
public interface WeddingAttenderRepository extends JpaRepository<WeddingAttender, Long> {

  /**
   * 参加者数に対する総計を取得する。
   * 
   * @return 参加者数の総計
   */
  @Query(value = "SELECT SUM(attenderNumber) FROM WeddingAttender")
  public int sumAttenderNumber();
}
