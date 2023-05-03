package com.my.azusato.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.my.azusato.entity.WeddingAttender;

@Repository
public interface WeddingAttenderRepository extends JpaRepository<WeddingAttender, Long> {

  /**
   * 参加する人に対して参加者数の総計を取得する。
   * 
   * @return 参加者数の総計
   */
  @Query(value = "SELECT SUM(attenderNumber) FROM WeddingAttender WHERE attend = TRUE")
  public int sumAttenderNumber();
}
