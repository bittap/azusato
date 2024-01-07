package com.my.azusato.api.controller;

import java.util.Objects;
import javax.servlet.http.HttpServletRequest;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import com.my.azusato.annotation.MethodAnnotation;
import com.my.azusato.api.controller.request.AddCelebrationReplyAPIReqeust;
import com.my.azusato.api.service.CelebrationReplyServiceAPI;
import com.my.azusato.exception.AzusatoException;
import com.my.azusato.exception.ErrorResponse;
import com.my.azusato.login.LoginUser;
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
 * API for the celebration.
 * <ul>
 * <li>add a celebration.</li>
 * </ul>
 * 
 * @author kim-t
 *
 */
@RestController
@RequestMapping(value = Api.COMMON_REQUSET)
@RequiredArgsConstructor
@Tag(name = "お祝い書き込み")
public class CelebrationReplyControllerAPI {

  private final CelebrationReplyServiceAPI celeReplyAPIService;

  private final HttpServletRequest servletRequest;

  private final MessageSource messageSource;

  public static final String COMMON_URL = "celebration-reply";

  public static final String POST_URL = COMMON_URL + "/{celebrationNo}";

  public static final String DELETE_URL = COMMON_URL + "/{celebrationReplyNo}";

  /**
   * @param req
   * @param loginUser
   */
  @Operation(summary = "API_cel_rep_001 お祝い書き込み追加",
      description = "「お祝い書き込み」とお祝いに関わる人に対して「お祝い通知」を登録する。")
  @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "お祝い書き込み成功"),
      @ApiResponse(responseCode = "400", description = "" //
          + "・ ユーザ情報、お祝い情報が存在しない。<br>" //
          + "・ パラメータがエラー", //
          content = {@Content(mediaType = "application/json",
              schema = @Schema(implementation = ErrorResponse.class))}),
      @ApiResponse(responseCode = "401", description = "ログインしていない",
          content = {@Content(mediaType = "application/json",
              schema = @Schema(implementation = ErrorResponse.class))}),})
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping(POST_URL)
  @MethodAnnotation(description = "API_cel_rep_001 お祝い書き込み追加")
  public void add( //
      @Parameter(name = "お祝い書き込み情報", required = true) //
      @RequestBody @Validated AddCelebrationReplyAPIReqeust req, //
      @Parameter(name = "お祝い番号", required = true) //
      @PathVariable(name = "celebrationNo", required = true) Long celebationNo, //
      @Parameter(name = "ログインしたユーザ情報", required = true) //
      @AuthenticationPrincipal LoginUser loginUser) {
    if (Objects.isNull(loginUser)) {
      throw new AzusatoException(HttpStatus.UNAUTHORIZED, AzusatoException.I0001,
          messageSource.getMessage(AzusatoException.I0001, null, servletRequest.getLocale()));
    }
    celeReplyAPIService.addCelebartionReply(req, celebationNo, loginUser.getUSER_NO(),
        servletRequest.getLocale());
  }

  /**
   * 「お祝い書き込み」を論理削除する。deletedをtrueに変更
   * 
   * @param celebrationReplyNo
   * @param loginUser
   */
  @Operation(summary = "API_cel_rep_002 お祝い書き込み削除", description = "「お祝い書き込み」を論理削除する。")
  @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "成功"),
      @ApiResponse(responseCode = "400", description = "" //
          + "・ 対象データ存在なし<br>" //
          + "・ 生成したユーザではない場合<br>" + //
          "・ パラメータがエラー", //
          content = {@Content(mediaType = "application/json",
              schema = @Schema(implementation = ErrorResponse.class))}),
      @ApiResponse(responseCode = "401", description = "ログインしていない",
          content = {@Content(mediaType = "application/json",
              schema = @Schema(implementation = ErrorResponse.class))}),})
  @ResponseStatus(HttpStatus.OK)
  @DeleteMapping(DELETE_URL)
  @MethodAnnotation(description = "API_cel_rep_002 お祝い書き込み削除")
  public void delete( //
      @Parameter(name = "お祝い書き込み番号", required = true) //
      @PathVariable(name = "celebrationReplyNo", required = true) Long celebrationReplyNo, //
      @Parameter(name = "ログインしたユーザ情報", required = true) //
      @AuthenticationPrincipal LoginUser loginUser) {
    if (Objects.isNull(loginUser)) {
      throw new AzusatoException(HttpStatus.UNAUTHORIZED, AzusatoException.I0001,
          messageSource.getMessage(AzusatoException.I0001, null, servletRequest.getLocale()));
    }
    celeReplyAPIService.deleteCelebartionReply(celebrationReplyNo, loginUser.getUSER_NO(),
        servletRequest.getLocale());
  }
}
