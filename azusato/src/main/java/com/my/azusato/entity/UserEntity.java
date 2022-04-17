package com.my.azusato.entity;

import javax.persistence.CascadeType;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

import com.my.azusato.entity.common.CommonDateEntity;
import com.my.azusato.entity.common.CommonFlagEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(name = "user")
@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserEntity {

	@GeneratedValue
	@Id
	private Long no;
	
	private String id;
	
	private String password;
	
	@PrimaryKeyJoinColumn
	@OneToOne(mappedBy = "user", cascade = CascadeType.ALL) // authorization for relative table
	private ProfileEntity profile;
	
	private String userType;
	
	@Embedded
	private CommonDateEntity commonDate;
	
	@Embedded
	private CommonFlagEntity commonFlag;
	
	@Getter
	public enum Type{
		admin,
		nonmember,
		kakao,
		line;
	}
}
