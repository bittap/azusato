package com.my.azusato.login;

import org.apache.logging.log4j.util.Strings;
import org.springframework.security.core.userdetails.User;
import com.my.azusato.entity.UserEntity;
import com.my.azusato.entity.UserEntity.Type;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class LoginUser extends User {

  private static final long serialVersionUID = 1L;

  /**
   * accountNonExpired,credentialsNonExpired等使用しないのは"true"
   */
  private static final boolean NOT_USED_FIELD = true;

  private final Long USER_NO;

  private static final String NOT_USE_PASSWORD = "NonMemberはパスワードを使用しない。";

  public LoginUser(UserEntity user) {
    super(user.getId(), Strings.isEmpty(user.getPassword()) ? NOT_USE_PASSWORD : user.getPassword(),
        !user.getCommonFlag().getDeleteFlag(), NOT_USED_FIELD, NOT_USED_FIELD, NOT_USED_FIELD,
        Grant.resolveRoles(Type.valueOf(user.getUserType())));

    this.USER_NO = user.getNo();
  }



}
