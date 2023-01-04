package com.my.azusato.api.service.response;

import java.util.List;
import com.my.azusato.entity.WeddingAttender;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class GetWeddingAttenderServiceAPIResponse {

  private List<WeddingAttender> weddingAttenders;

  private long total;
}
