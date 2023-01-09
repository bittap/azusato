package com.my.azusato.api.controller;

import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import com.my.azusato.annotation.MethodAnnotation;
import com.my.azusato.api.controller.request.CreateWeddingAttendRequest;
import com.my.azusato.api.controller.request.GetWeddingAttendsRequest;
import com.my.azusato.api.service.WeddingAttenderServiceAPI;
import com.my.azusato.api.service.response.GetWeddingAttenderServiceAPIResponse;
import com.my.azusato.view.controller.common.UrlConstant.Api;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(value = Api.COMMON_REQUSET + "wedding/")
@RequiredArgsConstructor
public class WeddingAttenderControllerAPI {

  private final WeddingAttenderServiceAPI weddingAttendService;

  @PostMapping("attender")
  @ResponseStatus(HttpStatus.CREATED)
  @MethodAnnotation(description = "API_wed_attend_001 結婚式参加の登録")
  public void create(@RequestBody @Validated CreateWeddingAttendRequest request) {
    weddingAttendService.create(request);
  }

  @GetMapping("attenders")
  @ResponseStatus(HttpStatus.OK)
  @MethodAnnotation(description = "API_wed_attend_002 結婚式参加者リスト")
  public GetWeddingAttenderServiceAPIResponse get(
      @ModelAttribute @Validated GetWeddingAttendsRequest request) {
    return weddingAttendService.get(request);
  }
}
