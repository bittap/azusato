package com.my.azusato.entity;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import com.my.azusato.entity.common.CommonDateEntity;
import com.my.azusato.entity.common.CommonFlagEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table(name = "profile")
@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProfileEntity {
	
	@Id
	@GeneratedValue
	private Long no;
	
	private String ImageBase64;
	
	private String ImageType;
	
	@Embedded
	private CommonDateEntity commonDate;
	
	@Embedded
	private CommonFlagEntity commonFlag;

}
