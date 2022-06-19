package com.my.azusato.entity;

import java.util.List;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.my.azusato.entity.common.CommonDateEntity;
import com.my.azusato.entity.common.CommonFlagEntity;
import com.my.azusato.entity.common.CommonUserEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table(name = "celebration")
@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CelebrationEntity {
	
	/**
	 * message.propertyのテーブルネームのキー
	 */
	public static final String TABLE_NAME_KEY = "table.name.celebation";
	
	@Id
	@GeneratedValue
	private Long no;
	
	private String title;
	
	private String contentPath;
	
	private Integer readCount;
	
	@OneToMany(mappedBy = "celebrationNo", fetch = FetchType.LAZY)
	private List<CelebrationReplyEntity> replys;
	
	@Embedded
	private CommonUserEntity commonUser;
	
	@Embedded
	private CommonDateEntity commonDate;
	
	@Embedded
	private CommonFlagEntity commonFlag;
}
