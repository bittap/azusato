package com.my.azusato.api.controller.request;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import com.my.azusato.entity.WeddingAttender.Division;
import com.my.azusato.entity.WeddingAttender.Nationality;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@Builder
@NoArgsConstructor
public class GetWeddingAttendsRequest {

  // TODO Enumのバリデーションチェック
  @NotNull(message = "{notNull}")
  private Nationality nationality;

  @NotNull(message = "{notNull}")
  private Boolean attend;

  @NotNull(message = "{notNull}")
  private Boolean eatting;

  // TODO Enumのバリデーションチェック
  @Size(max = 10, message = "{size-string.max}")
  private Division division;

  @NotNull(message = "{notNull}")
  private Integer offset;

  @Min(value = 1, message = "{min}")
  @NotNull(message = "{notNull}")
  private Integer limit;
}
