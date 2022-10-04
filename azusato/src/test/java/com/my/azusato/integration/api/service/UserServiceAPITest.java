package com.my.azusato.integration.api.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.time.LocalDateTime;
import java.util.Locale;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import com.my.azusato.anonotation.IntegrationService;
import com.my.azusato.api.service.UserServiceAPI;
import com.my.azusato.api.service.request.AddNonMemberUserServiceAPIRequest;
import com.my.azusato.api.service.response.GetSessionUserServiceAPIResponse;
import com.my.azusato.common.TestConstant.Entity;
import com.my.azusato.entity.UserEntity;
import com.my.azusato.entity.UserEntity.Type;
import com.my.azusato.exception.AzusatoException;
import com.my.azusato.factory.EntityFactory;
import com.my.azusato.repository.UserRepository;
import com.my.azusato.view.controller.common.ValueConstant;

@Import(UserServiceAPI.class)
@IntegrationService
public class UserServiceAPITest {

  @Autowired
  UserServiceAPI userServiceAPI;

  @Autowired
  UserRepository userRepo;

  @Nested
  class getSessionUser {

    @Nested
    @DisplayName("正常系")
    class normal {
      @Test
      public void givenNormalValue_resultOk() throws Exception {
        GetSessionUserServiceAPIResponse result =
            userServiceAPI.getSessionUser(Long.valueOf(Entity.createdInts[0]), null);

        UserEntity expectedEntity = EntityFactory.userByNo1();
        GetSessionUserServiceAPIResponse expected = GetSessionUserServiceAPIResponse.builder()
            .id(expectedEntity.getId()).name(expectedEntity.getName())
            .profileImagePath(expectedEntity.getProfile().getImagePath()).build();

        assertEquals(expected, result);
      }
    }

    @Nested
    @DisplayName("準正常系")
    class subnormal {
      @ParameterizedTest
      @MethodSource("com.my.azusato.common.TestSource#I0005_user_Message")
      public void givenNotExistData_result500Error(Locale locale, String expectedMessage)
          throws Exception {
        AzusatoException expected =
            new AzusatoException(HttpStatus.BAD_REQUEST, AzusatoException.I0005, expectedMessage);
        AzusatoException result = Assertions.assertThrows(AzusatoException.class,
            () -> userServiceAPI.getSessionUser(9999999L, locale));

        assertEquals(expected, result);
      }
    }
  }

  @Nested
  class AddNonMember {

    @Test
    public void givenNormalValue_resultAddedNonMember() throws Exception {
      LocalDateTime beforeExecuteTime = LocalDateTime.now();
      AddNonMemberUserServiceAPIRequest serviceReq = AddNonMemberUserServiceAPIRequest.builder()
          .name(Entity.createdVarChars[0]).id(Entity.createdVarChars[1]).build();

      userServiceAPI.addNonMember(serviceReq);

      // 比較のために最後のインデックスを取得
      UserEntity result = userRepo.findAll().stream().reduce((e1, e2) -> e2).get();

      assertEquals(Entity.createdVarChars[1], result.getId());
      assertEquals(Entity.createdVarChars[0], result.getName());
      assertTrue(beforeExecuteTime.isBefore(result.getCommonDate().getCreateDatetime()));
      assertTrue(beforeExecuteTime.isBefore(result.getCommonDate().getUpdateDatetime()));
      assertEquals(Type.nonmember.toString(), result.getUserType());
      assertEquals(ValueConstant.DEFAULT_DELETE_FLAG, result.getCommonFlag().getDeleteFlag());

      /*
       * cookie test can't this because of not http invoking
       */
    }
  }
}
