package com.my.azusato.entity;

import java.io.Serializable;

import javax.persistence.Column;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CelebrationNoticePk implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Column(name = "celebration_no")
	private Long celebrationNo;
	
	@Column(name = "user_no")
	private Long userNo;
}
