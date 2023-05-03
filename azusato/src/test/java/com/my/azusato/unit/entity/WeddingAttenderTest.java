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
      byte attenderNumber = 10;

      @Test
      void ok() {
        LocalDateTime mockedValue = LocalDateTime.of(2023, 1, 1, 1, 1);

        try (MockedStatic<LocalDateTime> mockedLocalDate = mockStatic(LocalDateTime.class)) {
          mockedLocalDate.when(() -> LocalDateTime.now()).thenReturn(mockedValue);

          WeddingAttender created = vaild();
          Assertions.assertEquals(name, created.getName());
          Assertions.assertEquals(nationality, created.getNationality());
          Assertions.assertEquals(attend, created.getAttend());
          Assertions.assertEquals(eatting, created.getEatting());
          Assertions.assertEquals(remark, created.getRemark());
          Assertions.assertEquals(mockedValue, created.getCreatedDatetime());
          Assertions.assertEquals(attenderNumber, created.getAttenderNumber());
        }
      }

      WeddingAttender vaild() {
        return WeddingAttender.builder() //
            .name(name) //
            .nationality(nationality) //
            .attend(attend) //
            .eatting(eatting) //
            .remark(remark) //
            .attenderNumber(attenderNumber) //
            .build();
      }
    }

    @Nested
    @DisplayName("準正常系")
    class subnormal {

      @Test
      void nameIsNull() {
        Assertions.assertThrows(NullPointerException.class, () -> {
          WeddingAttender.builder().name(null).build();
        });
      }

      @Test
      void nationalityIsNull() {
        Assertions.assertThrows(NullPointerException.class, () -> {
          WeddingAttender.builder().nationality(null).build();
        });
      }

      @Test
      void attendIsNull() {
        Assertions.assertThrows(NullPointerException.class, () -> {
          WeddingAttender.builder().attend(null).build();
        });
      }

      @Test
      void eattingIsNull() {
        Assertions.assertThrows(NullPointerException.class, () -> {
          WeddingAttender.builder().eatting(null).build();
        });
      }

      @Test
      void attenderNumberIsNull() {
        Assertions.assertThrows(NullPointerException.class, () -> {
          WeddingAttender.builder().attenderNumber(null).build();
        });
      }
    }
  }
}
