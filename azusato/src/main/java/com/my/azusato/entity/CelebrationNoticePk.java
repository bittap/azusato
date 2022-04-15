package com.my.azusato.entity;

import java.io.Serializable;

import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

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
	
	@ManyToOne
	@JoinColumn(name = "celerbration_no", referencedColumnName = "no")
	private CelebrationEntity celebrationEntity;
	
	@OneToOne
	@JoinColumn(name = "user_no", referencedColumnName = "no")
	private UserEntity userEntitys;
}
