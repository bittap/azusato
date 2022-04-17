package com.my.azusato.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "celebration_notice")
@Data
@NoArgsConstructor
@AllArgsConstructor 
@Builder
@IdClass(CelebrationNoticePk.class)
public class CelebrationNoticeEntity {
	
	@Id
	private Long celebrationNo;
	
	@Id
	private Long userNo;
}
