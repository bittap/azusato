package com.my.azusato.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

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
@DynamicInsert
@DynamicUpdate
public class UserEntity {

	@GeneratedValue
	@Id
	private Integer no;
	
	private String id;
	
	private String password;
	
	@OneToOne
	@JoinColumn(name = "profile_no", referencedColumnName = "no")
	private ProfileEntity profile;
	
	@Column(columnDefinition = "ENUM('admin', 'nonmember', 'kakao','line')")
	@Enumerated(EnumType.STRING)
	private Type userType;
	
	private LocalDateTime createDatetime;
	
	private LocalDateTime updateDatetime;
	
	private	Boolean deleteFlag;
	
	@Getter
	public enum Type{
		admin,
		nonmember,
		kakao,
		line;
	}
}
