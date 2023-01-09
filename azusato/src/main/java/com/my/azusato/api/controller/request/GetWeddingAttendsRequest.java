package com.my.azusato.api.controller.request;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import com.my.azusato.entity.WeddingAttender.Nationality;
import com.my.azusato.validator.annotation.ValueOfEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.bytebuddy.implementation.bytecode.Division;

@AllArgsConstructor
@Data
@Builder
@NoArgsConstructor
public class GetWeddingAttendsRequest {

  @ValueOfEnum(enumClass = Nationality.class)
  private String nationality;

  private Boolean attend;

  private Boolean eatting;

  @ValueOfEnum(enumClass = Division.class)
  private String division;

  private Boolean remarkNonNull;

  @NotNull(message = "{notNull}")
  private Integer offset;

  @Min(value = 1, message = "{min}")
  @NotNull(message = "{notNull}")
  private Integer limit;
}
