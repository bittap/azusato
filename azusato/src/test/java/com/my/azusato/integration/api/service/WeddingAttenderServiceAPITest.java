package com.my.azusato.integration.api.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import com.my.azusato.anonotation.IntegrationService;
import com.my.azusato.api.controller.request.CreateWeddingAttendRequest;
import com.my.azusato.api.controller.request.GetWeddingAttendsRequest;
import com.my.azusato.api.service.WeddingAttenderServiceAPI;
import com.my.azusato.entity.WeddingAttender;
import com.my.azusato.entity.WeddingAttender.Division;
import com.my.azusato.entity.WeddingAttender.Nationality;
import com.my.azusato.repository.WeddingAttenderRepository;

@IntegrationService
public class WeddingAttenderServiceAPITest {

  @Autowired
  WeddingAttenderServiceAPI service;

  @Autowired
  WeddingAttenderRepository repo;

  @Value("${wedding.division-date}")
  private String divisionDate;

  @Nested
  class create {

    @Nested
    @DisplayName("正常系")
    class normal {

      String name = "name";
      String nationality = Nationality.KOREA.toString();
      boolean attend = true;
      boolean eatting = true;
      String remark = "remark";
      byte attenderNumber = 10;

      @Test
      void ok() {
        int beforeSize = repo.findAll().size();
        CreateWeddingAttendRequest req = new CreateWeddingAttendRequest(name, nationality, attend,
            eatting, remark, attenderNumber);
        service.create(req);

        Assertions.assertEquals(beforeSize + 1, repo.findAll().size());
      }
    }
  }

  @Nested
  class get {

    @Nested
    @DisplayName("正常系")
    class normal {

      int offset = 0;

      int limit = Integer.MAX_VALUE;

      @Nested
      class where {

        @ParameterizedTest
        @EnumSource(value = Nationality.class)
        void givenParameterNationality_resultSorted(Nationality nationality) {
          var req = GetWeddingAttendsRequest.builder().nationality(nationality.toString())
              .offset(offset).limit(limit).build();

          var result = service.get(req);

          result.getWeddingAttenders().stream().forEach(
              e -> Assertions.assertEquals(nationality.toString(), e.getNationality().toString()));
          Assertions.assertEquals(result.getWeddingAttenders().size(), result.getTotal());
        }

        @ParameterizedTest
        @ValueSource(booleans = {true, false})
        void givenParameterAttend_resultSorted(Boolean attend) {
          var req =
              GetWeddingAttendsRequest.builder().attend(attend).offset(offset).limit(limit).build();

          var result = service.get(req);

          result.getWeddingAttenders().stream()
              .forEach(e -> Assertions.assertEquals(attend, e.getAttend()));
          Assertions.assertEquals(result.getWeddingAttenders().size(), result.getTotal());
        }

        @ParameterizedTest
        @ValueSource(booleans = {true, false})
        void givenParameterEatting_resultSorted(Boolean eatting) {
          var req = GetWeddingAttendsRequest.builder().eatting(eatting).offset(offset).limit(limit)
              .build();

          var result = service.get(req);

          result.getWeddingAttenders().stream()
              .forEach(e -> Assertions.assertEquals(eatting, e.getEatting()));
          Assertions.assertEquals(result.getWeddingAttenders().size(), result.getTotal());
        }

        @Test
        void givenDivisionFirst_resultSorted() {
          var req = GetWeddingAttendsRequest.builder().division(Division.FIRST.name())
              .offset(offset).limit(limit).build();

          var result = service.get(req);

          result.getWeddingAttenders().stream().forEach(
              e -> Assertions.assertTrue(e.getCreatedDatetime().isBefore(getDivisionDatetime())));
          Assertions.assertEquals(result.getWeddingAttenders().size(), result.getTotal());
        }

        @Test
        void givenbeforeWeddingDivisionDatetimeFalse_resultSorted() {
          var req = GetWeddingAttendsRequest.builder().division(Division.SECOND.name())
              .offset(offset).limit(limit).build();

          var result = service.get(req);

          result.getWeddingAttenders().stream().forEach(
              e -> Assertions.assertTrue(e.getCreatedDatetime().isEqual(getDivisionDatetime())
                  || e.getCreatedDatetime().isAfter(getDivisionDatetime())));
          Assertions.assertEquals(result.getWeddingAttenders().size(), result.getTotal());
        }

        @ParameterizedTest
        @ValueSource(booleans = {true, false})
        void givenParameterRemarkNonNull_resultSorted(Boolean remarkNonNull) {
          var req = GetWeddingAttendsRequest.builder().remarkNonNull(remarkNonNull).offset(offset)
              .limit(limit).build();

          var result = service.get(req);

          result.getWeddingAttenders().stream().forEach(e -> {
            if (remarkNonNull) {
              Assertions.assertTrue(e.getRemark().length() > 0, "remark length is 0");
            } else {
              Assertions.assertNull(e.getRemark(), "remark is nonNull");
            }
          });
          Assertions.assertEquals(result.getWeddingAttenders().size(), result.getTotal());
        }

        LocalDateTime getDivisionDatetime() {
          return LocalDate.parse(divisionDate, DateTimeFormatter.ISO_LOCAL_DATE).atStartOfDay();
        }

      }

      @Nested
      class paging {

        @Test
        void givenLimit1_resultOneSelected() {
          int expected = 1;

          var req = GetWeddingAttendsRequest.builder().offset(offset).limit(expected).build();
          var result = service.get(req);

          Assertions.assertEquals(expected, result.getWeddingAttenders().size());
        }

        @Test
        void givenOffsetInfinite_resultNoSelected() {
          var req =
              GetWeddingAttendsRequest.builder().offset(Integer.MAX_VALUE).limit(limit).build();
          var result = service.get(req);

          Assertions.assertEquals(0, result.getWeddingAttenders().size());
          Assertions.assertTrue(result.getTotal() > 0);
        }

        @Test
        void givenNoParameter_resultAllSelected() {
          int expected = repo.findAll().size();

          var req = GetWeddingAttendsRequest.builder().offset(offset).limit(limit).build();
          var result = service.get(req);

          Assertions.assertEquals(expected, result.getWeddingAttenders().size());
          Assertions.assertEquals(expected, result.getTotal());
        }
      }

      @Nested
      class orderBy {

        @Test
        void resultOrderByNoDesc() {
          List<WeddingAttender> expected = repo.findAll().stream()
              .sorted((e1, e2) -> (int) (e2.getNo() - e1.getNo())).collect(Collectors.toList());

          var req = GetWeddingAttendsRequest.builder().offset(offset).limit(limit).build();
          var result = service.get(req);

          Assertions.assertEquals(expected, result.getWeddingAttenders());
          Assertions.assertEquals(expected.size(), result.getTotal());
        }
      }
    }
  }
}
