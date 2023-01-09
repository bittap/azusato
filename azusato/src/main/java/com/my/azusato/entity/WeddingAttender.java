package com.my.azusato.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import org.hibernate.annotations.Comment;
import com.my.azusato.entity.base.BaseEntity;
import com.my.azusato.provider.ApplicationContextProvider;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

@Entity
@Table(name = "wedding_attender")
@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class WeddingAttender extends BaseEntity {

  @Column(length = 10, nullable = false)
  @Comment("ネーム")
  private String name;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, columnDefinition = "ENUM('KOREA', 'JAPAN', 'ETC')")
  @Comment("国籍")
  private Nationality nationality;

  @Column(nullable = false)
  @Comment("参加有無")
  private Boolean attend;

  @Column(nullable = false)
  @Comment("食事有無")
  private Boolean eatting;

  @Column(length = 1000, nullable = true)
  @Comment("備考")
  private String remark;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, columnDefinition = "ENUM('FIRST', 'SECOND')")
  @Comment("区分")
  private Division division;

  @Column(nullable = false)
  @Comment("生成日時")
  private LocalDateTime createdDatetime;

  @Builder
  private static WeddingAttender create(@NonNull String name, @NonNull Nationality nationality,
      @NonNull Boolean attend, @NonNull Boolean eatting, String remark) {
    WeddingAttender weddingAttend = new WeddingAttender();
    weddingAttend.name = name;
    weddingAttend.nationality = nationality;
    weddingAttend.attend = attend;
    weddingAttend.eatting = eatting;
    weddingAttend.remark = remark;
    weddingAttend.createdDatetime = LocalDateTime.now();
    weddingAttend.division = Division.valueOf(LocalDate.from(weddingAttend.createdDatetime));
    return weddingAttend;
  }

  /*
   * 区分 例)結婚式に参加するか2回確認する時1回目と2回目を分けるため。 一回目：2023年5月~2023年8月 2回目：2023年9月
   */
  public enum Division {
    FIRST, SECOND;


    /**
     * 生成日時と環境変数の{@code wedding-division-datetime}比較で{@link Division}を返す。
     * 
     * @param createdDate 生成日時
     * @return 生成日時 >= {@code wedding-division-datetime} : {@link Division#SECOND} , その以外 :
     *         {@link Division#FIRST}
     */
    public static Division valueOf(LocalDate createdDate) {
      LocalDate divisionDatetime =
          LocalDate.parse(ApplicationContextProvider.getApplicationContext().getEnvironment()
              .getProperty("wedding.division-datetime"), DateTimeFormatter.ISO_LOCAL_DATE);

      return createdDate.isEqual(divisionDatetime) || createdDate.isAfter(divisionDatetime)
          ? Division.SECOND
          : Division.FIRST;
    }
  }


  /**
   * 国籍
   * 
   * @author Carmel
   *
   */
  public enum Nationality {
    KOREA, JAPAN, ETC;
  }
}
