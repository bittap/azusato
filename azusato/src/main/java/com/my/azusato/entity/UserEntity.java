package com.my.azusato.entity;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
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
	
	@OneToOne(cascade = CascadeType.ALL) // authorization for relative table
	@JoinColumn(name = "profile_no", referencedColumnName = "no")
	private ProfileEntity profile;
	
	@Column(columnDefinition = "ENUM('admin', 'nonmember', 'kakao','line')")
	@Enumerated(EnumType.STRING)
	private Type userType;
	
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