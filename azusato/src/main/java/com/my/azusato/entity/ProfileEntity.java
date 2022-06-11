package com.my.azusato.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Table(name = "profile")
@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(exclude = "user") // for junit equal test
@Builder
public class ProfileEntity {
	
	@Id
	@Column(name = "user_no")
	private Long userNo;
	
	private String imageSrc;
	
	@OneToOne
	@JoinColumn(name = "user_no")
	@MapsId // for referencing user table
	@ToString.Exclude // for avoiding infinite loop
	private UserEntity user;

}
