package com.my.azusato.login;

import org.springframework.security.core.userdetails.User;

import com.my.azusato.entity.UserEntity;
import com.my.azusato.entity.UserEntity.Type;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class LoginUser extends User {
	
	private static final long serialVersionUID = 1L;

	/**
	 * accountNonExpired,credentialsNonExpired等使用しないのは"true"
	 */
	private static final boolean NOT_USED_FIELD = true;

	public LoginUser(UserEntity user,Grant grant) {
		super(user.getId(), user.getPassword(), !user.getCommonFlag().getDeleteFlag(), 
				NOT_USED_FIELD, NOT_USED_FIELD, NOT_USED_FIELD, grant.resolveRoles(Type.valueOf(user.getUserType())));
	}
	
	
	

}
