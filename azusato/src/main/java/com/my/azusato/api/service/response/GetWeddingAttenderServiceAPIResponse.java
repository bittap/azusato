package com.my.azusato.api.service.response;

import java.util.List;
import com.my.azusato.entity.WeddingAttender;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@EqualsAndHashCode
public class GetWeddingAttenderServiceAPIResponse {

  private List<WeddingAttender> weddingAttenders;

  private long total;

  private int totalAttenderNumber;
}
