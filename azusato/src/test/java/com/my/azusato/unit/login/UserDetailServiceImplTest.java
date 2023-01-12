package com.my.azusato.unit.login;

import java.util.stream.Stream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import com.my.azusato.common.TestConstant.Entity;
import com.my.azusato.entity.UserEntity;
import com.my.azusato.integration.AbstractIntegration;
import com.my.azusato.login.UserDetailServiceImpl;

public class UserDetailServiceImplTest extends AbstractIntegration {

  @Autowired
  UserDetailServiceImpl userDetailServiceImpl;

  @Nested
  class Login {

    final String USER_NAME = Entity.createdVarChars[0];

    @ParameterizedTest
    @MethodSource("com.my.azusato.unit.login.UserDetailServiceImplTest#Login_thenRealtiveUser_resultOk")
    public void thenRealtiveUser_resultOk(Long userNo) throws Exception {
      UserEntity expected = userRepo.findById(userNo).get();
      userDetailServiceImpl.loadUserByUsername(expected.getId());
    }

    @Test
    public void givenNotExistData_resultUsernameNotFoundException() throws Exception {
      Assertions.assertThrows(UsernameNotFoundException.class,
          () -> userDetailServiceImpl.loadUserByUsername("NotExistUser"));
    }
  }

  public static Stream<Long> Login_thenRealtiveUser_resultOk() {
    return Stream.of(Entity.ADMIN_USER_NOS[0], Entity.NONMEMBER_USER_NOS[0]);
  }
}
