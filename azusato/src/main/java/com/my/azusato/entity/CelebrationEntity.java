package com.my.azusato.entity;

import java.util.List;
import java.util.Set;

import javax.persistence.Embedded;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToMany;

import com.my.azusato.entity.common.CommonDateEntity;
import com.my.azusato.entity.common.CommonFlagEntity;
import com.my.azusato.entity.common.CommonUserEntity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * blobタイプのcontentがlazy検索ができないため、クラスを分ける。
 * contentが必要な場合は{@link CelebrationContentEntity}、要らない場合は、{@link CelebrationSummaryEntity}
 * @author Carmel
 *
 */
@Data
@MappedSuperclass
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
public class CelebrationEntity {
	
	/**
	 * message.propertyのテーブルネームのキー
	 */
	public static final String TABLE_NAME_KEY = "table.name.celebation";
	
	@Id
	@GeneratedValue
	private Long no;
	
	private String title;
	
	private Integer readCount;
	
	@OneToMany
	@JoinTable(
		name = "celebration_notice",
		joinColumns = @JoinColumn(name="celebration_no"),
		inverseJoinColumns = @JoinColumn(name="user_no")
	)
	private Set<UserEntity> notices;
	
	@OneToMany(mappedBy = "celebrationNo")
	private List<CelebrationReplyEntity> replys;
	
	@Embedded
	private CommonUserEntity commonUser;
	
	@Embedded
	private CommonDateEntity commonDate;
	
	@Embedded
	private CommonFlagEntity commonFlag;
}
