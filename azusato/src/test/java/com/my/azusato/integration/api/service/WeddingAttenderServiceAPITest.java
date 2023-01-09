package com.my.azusato.integration.api.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import com.my.azusato.anonotation.IntegrationService;
import com.my.azusato.api.controller.request.CreateWeddingAttendRequest;
import com.my.azusato.api.service.WeddingAttenderServiceAPI;
import com.my.azusato.entity.WeddingAttender.Nationality;
import com.my.azusato.repository.WeddingAttenderRepository;

@IntegrationService
public class WeddingAttenderServiceAPITest {

  @Autowired
  WeddingAttenderServiceAPI service;

  @Autowired
  WeddingAttenderRepository repo;

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

      @Test
      void ok() {
        int beforeSize = repo.findAll().size();
        CreateWeddingAttendRequest req =
            new CreateWeddingAttendRequest(name, nationality, attend, eatting, remark);
        service.create(req);

        Assertions.assertEquals(beforeSize + 1, repo.findAll().size());
      }
    }
  }
}
