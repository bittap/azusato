package com.my.azusato.api.controller;

import java.util.Objects;
import javax.servlet.http.HttpServletRequest;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import com.my.azusato.annotation.MethodAnnotation;
import com.my.azusato.api.controller.request.MyPageControllerRequest;
import com.my.azusato.api.service.CelebrationNoticeServiceAPI;
import com.my.azusato.api.service.request.GetCelebrationsSerivceAPIRequset;
import com.my.azusato.api.service.response.GetCelebrationNoticesSerivceAPIResponse;
import com.my.azusato.exception.AzusatoException;
import com.my.azusato.exception.ErrorResponse;
import com.my.azusato.login.LoginUser;
import com.my.azusato.property.CelebrationNoticeProperty;
import com.my.azusato.view.controller.common.UrlConstant.Api;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

/**
 * 
 * @author Carmel
 *
 */
@RestController
@RequestMapping(value = Api.COMMON_REQUSET)
@RequiredArgsConstructor
@Tag(name = "お祝い通知")
public class CelebrationNoticeControllerAPI {

  private final MessageSource messageSource;

  public static final String COMMON_URL = "celebration-notice";

  public static final String CELEBRATION_NOTICES_URL = COMMON_URL + "s";

  public static final String READCOUNTUP_URL = COMMON_URL + "/read" + "/{celebrationNo}";

  private final CelebrationNoticeServiceAPI celeNotiAPIService;

  private final HttpServletRequest servletRequest;

  private final CelebrationNoticeProperty noticeProperty;


  @Operation(summary = "API_cel-noti_001 お祝い通知リスト情報取得", description = "お祝い通知リスト情報を取得する")
  @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "お祝い通知リスト情報"),
      @ApiResponse(responseCode = "401", description = "ログインしていない",
          content = {@Content(mediaType = "application/json",
              schema = @Schema(implementation = ErrorResponse.class))}),})
  @ResponseStatus(HttpStatus.OK)
  @GetMapping(CELEBRATION_NOTICES_URL)
  @MethodAnnotation(description = "API_cel-noti_001 お祝い通知リスト情報取得")
  public GetCelebrationNoticesSerivceAPIResponse celebrationNotices( //
      @Parameter(name = "ログインしたユーザ情報", required = true) //
      @AuthenticationPrincipal LoginUser loginUser) {
    if (Objects.isNull(loginUser))
      throw new AzusatoException(HttpStatus.UNAUTHORIZED, AzusatoException.I0001,
          messageSource.getMessage(AzusatoException.I0001, null, servletRequest.getLocale()));

    final int CURRENT_PAGE = 1;
    MyPageControllerRequest page = MyPageControllerRequest.builder().currentPageNo(CURRENT_PAGE)
        .pageOfElement(noticeProperty.getPageOfElement()).build();

    GetCelebrationsSerivceAPIRequset serviceReq =
        GetCelebrationsSerivceAPIRequset.builder().pageReq(page).build();

    return celeNotiAPIService.celebrationNotices(serviceReq, loginUser.getUSER_NO());
  }

  /**
   * @param celebationNo お祝い番号
   */
  @Operation(summary = "API_cel-noti_002 お祝い通知既読処理", description = "お祝い通知を既読に変更する処理")
  @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "成功"),
      @ApiResponse(responseCode = "400", description = "対象データ存在なし", //
          content = {@Content(mediaType = "application/json",
              schema = @Schema(implementation = ErrorResponse.class))}),
      @ApiResponse(responseCode = "401", description = "ログインしていない",
          content = {@Content(mediaType = "application/json",
              schema = @Schema(implementation = ErrorResponse.class))}),})
  @ResponseStatus(HttpStatus.OK)
  @PutMapping(READCOUNTUP_URL)
  @MethodAnnotation(description = "API_cel-noti_002 お祝い通知既読処理")
  public void read( //
      @Parameter(name = "お祝い番号", required = true) //
      @PathVariable(name = "celebrationNo", required = true) Long celebationNo, //
      @Parameter(name = "ログインしたユーザ情報", required = true) //
      @AuthenticationPrincipal LoginUser loginUser) {
    if (Objects.isNull(loginUser))
      throw new AzusatoException(HttpStatus.UNAUTHORIZED, AzusatoException.I0001,
          messageSource.getMessage(AzusatoException.I0001, null, servletRequest.getLocale()));

    celeNotiAPIService.read(celebationNo, loginUser.getUSER_NO(), servletRequest.getLocale());
  }
}
