package com.my.azusato.api.controller;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.io.FilenameUtils;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import com.my.azusato.annotation.MethodAnnotation;
import com.my.azusato.api.controller.request.UploadProfileImageAPIRequest;
import com.my.azusato.api.controller.response.DefaultRandomProfileResponse;
import com.my.azusato.api.service.ProfileServiceAPI;
import com.my.azusato.api.service.request.ModifyUserProfileServiceAPIRequest;
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
import lombok.extern.slf4j.Slf4j;

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
@Slf4j
@RequiredArgsConstructor
@Tag(name = "プロフィール")
public class ProfileControllerAPI {

  public static final String COMMON_URL = "profile";

  public static final String RANDOM_URL = COMMON_URL + "/random";

  public static final String UPLOAD_IMG_URL = COMMON_URL + "/upload-img";

  public static final List<String> PERMIT_IMAGE_TYPES = List.of("png", "jpeg", "jpg");

  private final ProfileServiceAPI profileService;

  private final MessageSource ms;

  private final HttpServletRequest servletRequest;

  /**
   * 既に登録したランダムイメージの情報を取得する。
   * 
   * @return {@link DefaultRandomProfileResponse}
   */
  @Operation(summary = "API_pro_001 ランダムイメージの情報取得", description = "既に登録したランダムイメージの情報を取得する。")
  @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "プロフィールパス"),})
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  @GetMapping(value = RANDOM_URL)
  @MethodAnnotation(description = "API_pro_001 ランダムイメージの情報取得")
  public DefaultRandomProfileResponse getDefaultRandomProfile() {
    log.debug("[ランダムイメージ取得] START");
    DefaultRandomProfileResponse response = new DefaultRandomProfileResponse();

    response.setProfileImagePath(profileService.getDefaultProfilePath(servletRequest.getLocale()));

    log.debug("[ランダムイメージ取得] END response : {}", response);
    return response;
  }

  /**
   * @param req
   * @param loginUser
   * @throws IOException
   */
  @Operation(summary = "API_pro_002 イメージのアップロード&更新", description = "イメージのアップロード&更新を行う。")
  @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "成功"),
      @ApiResponse(responseCode = "400", description = "" //
          + "・ パラメータがnull<br>" //
          + "・ サポートしない拡張子", //
          content = {@Content(mediaType = "application/json",
              schema = @Schema(implementation = ErrorResponse.class))}),
      @ApiResponse(responseCode = "401", description = "ログインしていない",
          content = {@Content(mediaType = "application/json",
              schema = @Schema(implementation = ErrorResponse.class))}),})
  @ResponseStatus(HttpStatus.OK)
  @PostMapping(value = UPLOAD_IMG_URL, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @MethodAnnotation(description = "API_pro_002 イメージのアップロード&更新")
  public void uploadImage( //
      @Parameter(name = "イメージファイルのデータ(MultipartFile)", required = true) //
      @Validated @ModelAttribute UploadProfileImageAPIRequest req, //
      @Parameter(name = "ログインしたユーザ情報", required = true) //
      @AuthenticationPrincipal LoginUser loginUser) throws IOException {
    String extension = FilenameUtils.getExtension(req.getProfileImage().getOriginalFilename());
    // ファイル拡張子チェック
    PERMIT_IMAGE_TYPES.stream().filter((e) -> e.equalsIgnoreCase(extension)).findFirst()
        .orElseThrow(() -> {
          String profileImageFieldName =
              ms.getMessage("profileImage", new String[] {}, servletRequest.getLocale());
          throw new AzusatoException(HttpStatus.BAD_REQUEST, AzusatoException.I0004,
              ms.getMessage("file-type-mismatch",
                  new String[] {extension, profileImageFieldName,
                      PERMIT_IMAGE_TYPES.stream().collect(Collectors.joining(","))},
                  servletRequest.getLocale()));
        });

    if (Objects.isNull(loginUser)) {
      throw new AzusatoException(HttpStatus.UNAUTHORIZED, AzusatoException.I0001,
          ms.getMessage(AzusatoException.I0001, null, servletRequest.getLocale()));
    }

    ModifyUserProfileServiceAPIRequest serviceReq = ModifyUserProfileServiceAPIRequest.builder()
        .userNo(loginUser.getUSER_NO()).profileImage(req.getProfileImage().getInputStream())
        .profileImageType(extension).build();


    profileService.updateUserProfile(serviceReq, servletRequest.getLocale());
  }
}
