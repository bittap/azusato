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
@Table(name = "celebration_reply_notice")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@IdClass(CelebrationReplyNoticePk.class)
public class CelebrationReplyNoticeEntity {

	@Id
	private Long celebrationReplyNo;
	
	@Id
	private Long userNo;
}
