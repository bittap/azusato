package com.my.azusato.entity;

import java.util.Set;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
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
@Table(name = "celebration")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CelebrationEntity {
	
	@Id
	@GeneratedValue
	private Long no;
	
	private String title;
	
	@Lob
	private String content;
	
	private Integer readCount;
	
	@OneToMany()
	@JoinTable(
		name = "celebration_notice",
		joinColumns = @JoinColumn(name="celebration_no"),
		inverseJoinColumns = @JoinColumn(name="user_no")
	)
	private Set<UserEntity> notices;
	
	@Embedded
	private CommonUserEntity commonUser;
	
	@Embedded
	private CommonDateEntity commonDate;
	
	@Embedded
	private CommonFlagEntity commonFlag;
}
