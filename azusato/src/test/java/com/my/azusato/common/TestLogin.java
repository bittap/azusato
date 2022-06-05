package com.my.azusato.common;

import com.my.azusato.common.TestConstant.Entity;
import com.my.azusato.entity.UserEntity;
import com.my.azusato.entity.UserEntity.Type;
import com.my.azusato.entity.common.CommonFlagEntity;
import com.my.azusato.login.Grant;
import com.my.azusato.login.LoginUser;
import com.my.azusato.view.controller.common.ValueConstant;

public class TestLogin {

	public static LoginUser adminLoginUser(Grant grant) {
		return new LoginUser(getUserEntity(Type.admin), grant);
	}
	
	public static LoginUser kakaoLoginUser(Grant grant) {
		return new LoginUser(getUserEntity(Type.kakao), grant);
	}
	
	public static LoginUser lineLoginUser(Grant grant) {
		return new LoginUser(getUserEntity(Type.line), grant);
	}
	
	public static LoginUser nonmemberLoginUser(Grant grant) {
		return new LoginUser(getUserEntity(Type.nonmember), grant);
	}
	
	private static UserEntity getUserEntity(Type type) {
		UserEntity userEntity = new UserEntity();
		userEntity.setId(Entity.createdVarChars[0]);
		userEntity.setPassword(Entity.createdVarChars[1]);
		userEntity.setUserType(type.toString());
		userEntity.setCommonFlag(CommonFlagEntity.builder().deleteFlag(ValueConstant.DEFAULT_DELETE_FLAG).build());
		return userEntity;
	}
}
