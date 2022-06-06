package com.my.azusato.common;

import com.my.azusato.common.TestConstant.Entity;
import com.my.azusato.entity.UserEntity;
import com.my.azusato.entity.UserEntity.Type;
import com.my.azusato.entity.common.CommonFlagEntity;
import com.my.azusato.login.LoginUser;
import com.my.azusato.view.controller.common.ValueConstant;

public class TestLogin {

	public static LoginUser adminLoginUser() {
		return new LoginUser(getUserEntity(Type.admin));
	}
	
	public static LoginUser kakaoLoginUser() {
		return new LoginUser(getUserEntity(Type.kakao));
	}
	
	public static LoginUser lineLoginUser() {
		return new LoginUser(getUserEntity(Type.line));
	}
	
	public static LoginUser nonmemberLoginUser() {
		return new LoginUser(getUserEntity(Type.nonmember));
	}
	
	private static UserEntity getUserEntity(Type type) {
		UserEntity userEntity = new UserEntity();
		userEntity.setNo(Entity.createdLongs[0]);
		userEntity.setId(Entity.createdVarChars[0]);
		userEntity.setPassword(Entity.createdVarChars[1]);
		userEntity.setUserType(type.toString());
		userEntity.setCommonFlag(CommonFlagEntity.builder().deleteFlag(ValueConstant.DEFAULT_DELETE_FLAG).build());
		return userEntity;
	}
}
