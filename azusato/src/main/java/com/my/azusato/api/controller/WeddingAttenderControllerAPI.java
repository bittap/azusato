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
import com.my.azusato.exception.ErrorResponse;
import com.my.azusato.view.controller.common.UrlConstant.Api;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(value = Api.COMMON_REQUSET + "wedding/")
@RequiredArgsConstructor
@Tag(name = "結婚式参加者")
public class WeddingAttenderControllerAPI {

  private final WeddingAttenderServiceAPI weddingAttendService;

  @Operation(summary = "API_wed_attend_001 結婚式参加者の登録", description = "結婚式参加者の登録を行う")
  @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "成功"),
      @ApiResponse(responseCode = "400", description = "パラメータがエラー", //
          content = {@Content(mediaType = "application/json",
              schema = @Schema(implementation = ErrorResponse.class))})})
  @PostMapping("attender")
  @ResponseStatus(HttpStatus.CREATED)
  @MethodAnnotation(description = "API_wed_attend_001 結婚式参加者の登録")
  public void create( //
      @Parameter(description = "結婚式参加者情報", required = true) //
      @RequestBody @Validated CreateWeddingAttendRequest request) {
    weddingAttendService.create(request);
  }

  @Operation(summary = "API_wed_attend_002 結婚式参加者リスト", description = "結婚式参加者リストを取得する")
  @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "結婚式参加者リスト"),})
  @GetMapping("attenders")
  @ResponseStatus(HttpStatus.OK)
  @MethodAnnotation(description = "API_wed_attend_002 結婚式参加者リスト")
  public GetWeddingAttenderServiceAPIResponse get(
      @Parameter(description = "検索条件とページ情報", required = true) //
      @ModelAttribute @Validated GetWeddingAttendsRequest request) {
    return weddingAttendService.get(request);
  }
}
