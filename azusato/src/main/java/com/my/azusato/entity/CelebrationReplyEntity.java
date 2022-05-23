package com.my.azusato.entity;

import java.util.Set;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.my.azusato.entity.common.CommonDateEntity;
import com.my.azusato.entity.common.CommonFlagEntity;
import com.my.azusato.entity.common.CommonUserEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "celebration_reply")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CelebrationReplyEntity {
	
	/**
	 * message.propertyのテーブルネームのキー
	 */
	public static final String TABLE_NAME_KEY = "table.name.celebation-reply";
	
	@Id
	@GeneratedValue
	private Long no;

	private Long celebrationNo;
	
	private String content;
	
	@OneToMany
	@JoinTable(
			name = "celebration_reply_notice",
			joinColumns = @JoinColumn(name="celebration_reply_no"),
			inverseJoinColumns = @JoinColumn(name="user_no")
			
	)
	private Set<UserEntity> replyNotices;
	
	@Embedded
	private CommonUserEntity commonUser;
	
	@Embedded
	private CommonDateEntity commonDate;
	
	@Embedded
	private CommonFlagEntity commonFlag;
}
