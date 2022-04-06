package com.my.azusato.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import com.my.azusato.entity.common.CommonDateEntity;

import lombok.Data;

@Entity
@Table(name = "celerbration")
@Data
public class CelebrationEntity {
	
	@Id
	@GeneratedValue
	private Integer no;
	
	private String title;
	
	private String content;
	
	private Integer readCount;
	
	private CommonDateEntity commonEntity;
}
