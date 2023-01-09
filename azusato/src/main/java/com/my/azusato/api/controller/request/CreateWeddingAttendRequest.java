package com.my.azusato.api.controller.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import com.my.azusato.entity.WeddingAttender.Nationality;
import com.my.azusato.validator.annotation.ValueOfEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@Builder
@NoArgsConstructor
public class CreateWeddingAttendRequest {

  @NotBlank(message = "{notBlank}")
  @Size(max = 10, message = "{size-string.max}")
  private String name;

  @NotNull(message = "{notNull}")
  @ValueOfEnum(enumClass = Nationality.class)
  private String nationality;

  @NotNull(message = "{notNull}")
  private Boolean attend;

  @NotNull(message = "{notNull}")
  private Boolean eatting;

  @Size(max = 1000, message = "{size-string.max}")
  private String remark;
}
