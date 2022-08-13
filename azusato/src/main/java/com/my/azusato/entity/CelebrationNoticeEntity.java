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
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "celebration_notice")
@Data
@NoArgsConstructor
@AllArgsConstructor 
@Builder
@EqualsAndHashCode(exclude = {"celebration","reply"})
public class CelebrationNoticeEntity {
	
	/**
	 * message.propertyのテーブルネームのキー
	 */
	public static final String TABLE_NAME_KEY = "table.name.celebation-notice";
	
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
	@ToString.Exclude
	private CelebrationEntity celebration;
	
	/**
	 * celerbration_replyテーブルのFK
	 */
	@ManyToOne
	@JoinColumn(name = "celebration_reply_no")
	@ToString.Exclude
	private CelebrationReplyEntity reply;
	
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
