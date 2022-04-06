package com.my.azusato.entity.common;

import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

import com.my.azusato.entity.UserEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommonUserEntity {
	
	@OneToOne
	@JoinColumn(name = "create_user_no", referencedColumnName = "no")
	private UserEntity createUserEntity;
	
	@OneToOne
	@JoinColumn(name = "update_user_no", referencedColumnName = "no")
	private UserEntity updateUserEntity;
}
