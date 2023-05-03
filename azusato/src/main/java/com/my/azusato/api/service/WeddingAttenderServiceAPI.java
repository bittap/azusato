package com.my.azusato.api.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.my.azusato.api.controller.request.CreateWeddingAttendRequest;
import com.my.azusato.api.controller.request.GetWeddingAttendsRequest;
import com.my.azusato.api.service.response.GetWeddingAttenderServiceAPIResponse;
import com.my.azusato.entity.QWeddingAttender;
import com.my.azusato.entity.WeddingAttender;
import com.my.azusato.entity.WeddingAttender.Division;
import com.my.azusato.entity.WeddingAttender.Nationality;
import com.my.azusato.repository.WeddingAttenderRepository;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.JPQLQueryFactory;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WeddingAttenderServiceAPI {

  private final WeddingAttenderRepository weddingAttendRepository;

  @PersistenceContext
  private final EntityManager entityManger;

  @Value("${wedding.division-date}")
  private String divisionDate;

  @Transactional
  public void create(CreateWeddingAttendRequest request) {
    WeddingAttender weddingAttend = WeddingAttender.builder() //
        .name(request.getName()) //
        .nationality(Nationality.valueOf(request.getNationality())) //
        .attend(request.getAttend()) //
        .eatting(request.getEatting()) //
        .remark(request.getRemark()) //
        .attenderNumber(request.getAttenderNumber()) //
        .build();

    weddingAttendRepository.save(weddingAttend);
  }

  /**
   * 結婚式参加者リストを取得する。
   * 
   * @param request SELECTクエリの付加要素
   * @param weddingDivisionDatetime
   * @return 結婚式参加者リスト
   */
  @Transactional(readOnly = true)
  public GetWeddingAttenderServiceAPIResponse get(GetWeddingAttendsRequest request) {
    QWeddingAttender weddingAttender = QWeddingAttender.weddingAttender;
    JPQLQueryFactory qFactory = new JPAQueryFactory(entityManger);

    List<Predicate> wheres =
        getWheres(weddingAttender, request.getNationality(), request.getAttend(),
            request.getEatting(), request.getDivision(), request.getRemarkNonNull());

    QueryResults<WeddingAttender> result = qFactory.selectFrom(weddingAttender) //
        .where(wheres.toArray(new Predicate[wheres.size()])) //
        .orderBy(weddingAttender.no.desc()) //
        .offset(request.getOffset()) //
        .limit(request.getLimit()).fetchResults();

    return GetWeddingAttenderServiceAPIResponse.builder() //
        .weddingAttenders(result.getResults()) //
        .total(result.getTotal()) //
        .totalAttenderNumber(weddingAttendRepository.sumAttenderNumber()) //
        .build();
  }

  /**
   * 結婚式参加者リストのwhere文を作る。
   * 
   */
  private List<Predicate> getWheres(QWeddingAttender weddingAttender, String nationality,
      Boolean attend, Boolean eatting, String divisionString, Boolean remarkNonNull) {
    LocalDate divisionDatetime = LocalDate.parse(divisionDate, DateTimeFormatter.ISO_LOCAL_DATE);

    List<Predicate> wheres = new ArrayList<>();
    if (Objects.nonNull(nationality))
      wheres.add(weddingAttender.nationality.eq(Nationality.valueOf(nationality)));
    if (Objects.nonNull(attend))
      wheres.add(weddingAttender.attend.eq(attend));
    if (Objects.nonNull(eatting))
      wheres.add(weddingAttender.eatting.eq(eatting));

    if (Objects.nonNull(divisionString)) {
      Division division = Division.valueOf(divisionString);
      if (division == Division.FIRST)
        wheres.add(weddingAttender.createdDatetime.before(divisionDatetime.atStartOfDay()));
      else if (division == Division.SECOND)
        wheres.add(weddingAttender.createdDatetime.goe(divisionDatetime.atStartOfDay()));
    }


    if (Objects.nonNull(remarkNonNull) && remarkNonNull)
      wheres.add(weddingAttender.remark.isNotNull());
    else if (Objects.nonNull(remarkNonNull) && !remarkNonNull)
      wheres.add(weddingAttender.remark.isNull());

    return wheres;
  }
}
