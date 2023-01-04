package com.my.azusato.entity.base;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import org.hibernate.annotations.Comment;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@MappedSuperclass
@ToString
@EqualsAndHashCode
@Getter
public class BaseEntity {

  @Id
  @GeneratedValue
  @Comment("番号")
  private Long no;
}
