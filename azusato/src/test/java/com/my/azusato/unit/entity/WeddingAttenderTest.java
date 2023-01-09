package com.my.azusato.unit.entity;

import static org.mockito.Mockito.mockStatic;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import com.my.azusato.entity.WeddingAttender;
import com.my.azusato.entity.WeddingAttender.Nationality;

public class WeddingAttenderTest {

  @Nested
  class create {

    @Nested
    @DisplayName("正常系")
    class normal {

      String name = "name";
      Nationality nationality = Nationality.KOREA;
      boolean attend = true;
      boolean eatting = true;
      String remark = "remark";

      @Test
      void ok() {
        LocalDateTime mockedValue = LocalDateTime.of(2023, 1, 1, 1, 1);

        try (MockedStatic<LocalDateTime> mockedLocalDate = mockStatic(LocalDateTime.class)) {
          mockedLocalDate.when(() -> LocalDateTime.now()).thenReturn(mockedValue);

          WeddingAttender created = vaild();
          Assertions.assertEquals(created.getName(), name);
          Assertions.assertEquals(created.getNationality(), nationality);
          Assertions.assertEquals(created.getAttend(), attend);
          Assertions.assertEquals(created.getEatting(), eatting);
          Assertions.assertEquals(created.getRemark(), remark);
          Assertions.assertEquals(created.getCreatedDatetime(), mockedValue);
        }
      }

      WeddingAttender vaild() {
        return WeddingAttender.builder() //
            .name(name) //
            .nationality(nationality) //
            .attend(attend) //
            .eatting(eatting) //
            .remark(remark) //
            .build();
      }
    }

    @Nested
    @DisplayName("準正常系")
    class subnormal {

      @Test
      void givenNotNullParameter_resultDoesNotThrowException() {
        Assertions.assertDoesNotThrow(() -> {
          WeddingAttender.builder() //
              .name("name") //
              .nationality(Nationality.KOREA) //
              .attend(true) //
              .eatting(true) //
              .remark(null) //
              .build();
        });
      }

      @Test
      void givenNullParameter_resultThrowException() {
        Assertions.assertThrows(NullPointerException.class, () -> {
          WeddingAttender.builder().name(null).build();
          WeddingAttender.builder().nationality(null).build();
          WeddingAttender.builder().attend(null).build();
          WeddingAttender.builder().eatting(null).build();
        });
      }
    }
  }
}
