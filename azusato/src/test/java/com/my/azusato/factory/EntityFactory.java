package com.my.azusato.factory;

import com.my.azusato.common.TestConstant.Entity;
import com.my.azusato.entity.ProfileEntity;
import com.my.azusato.entity.UserEntity;
import com.my.azusato.entity.UserEntity.Type;
import com.my.azusato.entity.common.CommonDateEntity;
import com.my.azusato.entity.common.CommonFlagEntity;

/**
 * テストのためのEntityを作る。<br>
 * 値は{@link Entity}のフィールドによる。
 * 
 * @author Carmel
 *
 */
public class EntityFactory {

  public static UserEntity userByNo1() {
    return UserEntity.builder().no(Entity.createdLongs[0]) //
        .id(Entity.uniques[0]) //
        .name(Entity.createdVarChars[1]) //
        .password(Entity.createdVarChars[2]) //
        .userType(Type.admin.toString()) //
        .profile(ProfileEntity.builder().userNo(Entity.createdLongs[0])
            .imagePath(Entity.createdVarChars[0]).build()) //
        .commonDate(CommonDateEntity.builder().createDatetime(Entity.createdDatetimes[0])
            .updateDatetime(Entity.createdDatetimes[0]).build()) //
        .commonFlag(CommonFlagEntity.builder().deleteFlag(false).build())//
        .build();
  }
}
