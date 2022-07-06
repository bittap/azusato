package com.my.azusato.entity;

import java.time.LocalDateTime;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.my.azusato.entity.common.CommonDateEntity;
import com.my.azusato.entity.common.CommonFlagEntity;

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
public class CelebrationNoticeEntity {
	
	/**
	 * 番号
	 */
	@Id
	@GeneratedValue
	private Long no;
	
	/**
	 * celerbrationテーブルのFK
	 */
	@ManyToOne
	@JoinColumn(name = "celebration_no")
	private CelebrationEntity celebration;
	
	/**
	 * celerbration_replyテーブルのFK
	 */
	@ManyToOne
	@JoinColumn(name = "celebration_reply_no")
	private CelebrationReplyEntity Reply;
	
	/**
	 * 既読フラグ 既読すると : ture, 既読していない場合 : false
	 */
	private Boolean readed;
	
	@Embedded
	private CommonDateEntity commonDate;
	
	/**
	 * 既読した日時
	 */
	private LocalDateTime readDatetime;
	
	/**
	 * 通知対象ユーザ
	 */
	@OneToOne
	@JoinColumn(name = "target_user_no")
	private UserEntity targetUser;
	
	@Embedded
	private CommonFlagEntity commonFlag;
}
