package com.my.azusato.unit.entity;

import static org.mockito.Mockito.mockStatic;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.stream.Stream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.ConfigDataApplicationContextInitializer;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import com.my.azusato.common.TestConstant;
import com.my.azusato.entity.WeddingAttender;
import com.my.azusato.entity.WeddingAttender.Division;
import com.my.azusato.entity.WeddingAttender.Nationality;
import com.my.azusato.provider.ApplicationContextProvider;

@ExtendWith({SpringExtension.class, MockitoExtension.class})
@ContextConfiguration(initializers = ConfigDataApplicationContextInitializer.class) // yaml読み込む
@ActiveProfiles(value = TestConstant.PROFILES)
@Import(ApplicationContextProvider.class)
public class WeddingAttenderTest {

  @Value("${wedding.division-datetime}")
  private String weddingDivisionDatetime;

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
          Assertions.assertEquals(created.getDivision(), Division.FIRST);
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


  @Nested
  class DivisionTest {

    @Nested
    class valueOf {

      @Nested
      @DisplayName("正常系")
      class normal {

        @ParameterizedTest
        @MethodSource("com.my.azusato.unit.entity.WeddingAttenderTest#Division_valueOf_normal_ok")
        void ok(LocalDate createdDate, Division expected) {
          System.out.println("wedding-division-datetime : " + weddingDivisionDatetime);
          Division result = Division.valueOf(createdDate);
          Assertions.assertEquals(expected, result);
        }
      }
    }
  }

  public static Stream<Arguments> Division_valueOf_normal_ok() {
    return Stream.of(Arguments.of(LocalDate.of(2023, 1, 1), Division.FIRST),
        Arguments.of(LocalDate.of(2023, 5, 1), Division.SECOND));
  }
}
