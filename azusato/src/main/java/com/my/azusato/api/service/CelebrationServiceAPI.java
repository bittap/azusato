package com.my.azusato.api.service;

import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import org.apache.commons.io.IOUtils;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import com.my.azusato.annotation.MethodAnnotation;
import com.my.azusato.api.service.request.AddCelebrationServiceAPIRequest;
import com.my.azusato.api.service.request.GetCelebrationsSerivceAPIRequset;
import com.my.azusato.api.service.request.ModifyCelebationServiceAPIRequest;
import com.my.azusato.api.service.response.GetCelebrationContentSerivceAPIResponse;
import com.my.azusato.api.service.response.GetCelebrationContentSerivceAPIResponse.CelebrationReply;
import com.my.azusato.api.service.response.GetCelebrationSerivceAPIResponse;
import com.my.azusato.api.service.response.GetCelebrationsSerivceAPIResponse;
import com.my.azusato.api.service.response.GetCelebrationsSerivceAPIResponse.Celebration;
import com.my.azusato.entity.CelebrationEntity;
import com.my.azusato.entity.CelebrationNoticeEntity;
import com.my.azusato.entity.UserEntity;
import com.my.azusato.entity.UserEntity.Type;
import com.my.azusato.entity.common.CommonDateEntity;
import com.my.azusato.entity.common.CommonFlagEntity;
import com.my.azusato.entity.common.CommonUserEntity;
import com.my.azusato.entity.common.DefaultValueConstant;
import com.my.azusato.exception.AzusatoException;
import com.my.azusato.page.MyPageRequest;
import com.my.azusato.page.MyPaging;
import com.my.azusato.property.CelebrationProperty;
import com.my.azusato.repository.CelebrationNoticeRepository;
import com.my.azusato.repository.CelebrationRepository;
import com.my.azusato.repository.UserRepository;
import com.my.azusato.view.controller.common.ValueConstant;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * Service API for celebration.
 * <ul>
 * <li>add a celebration.</li>
 * </ul>
 * 
 * @author kim-t
 *
 */
@Service
@RequiredArgsConstructor
public class CelebrationServiceAPI {

  private final UserRepository userRepo;

  private final CelebrationProperty celeProperty;

  private final MessageSource messageSource;

  private final CelebrationRepository celeRepo;

  private final CelebrationNoticeRepository celeNotiRepo;

  /**
   * delegate
   * {@link CelebrationServiceAPI#addCelebartion(AddCelebrationServiceAPIRequest, Locale, String)}.
   * 
   * @param req request parameter.
   * @param locale locale of client.
   * @throws IOException
   */
  @MethodAnnotation(description = "お祝い情報登録(管理者)")
  public void addCelebartionAdmin(AddCelebrationServiceAPIRequest req, Locale locale)
      throws IOException {
    addCelebartion(req, locale, "admin");
  }

  /**
   * delegate
   * {@link CelebrationServiceAPI#addCelebartion(AddCelebrationServiceAPIRequest, Locale, String)}.
   * 
   * @param req request parameter.
   * @param locale locale of client.
   * @throws IOException
   */
  @MethodAnnotation(description = "お祝い情報登録(管理者以外)")
  public void addCelebartion(AddCelebrationServiceAPIRequest req, Locale locale)
      throws IOException {
    addCelebartion(req, locale, "not_admin");
  }

  /**
   * お祝い情報を登録する。 "userType"がadminではない場合は、管理者に通知するためにお祝い通知テーブルに通知情報を登録する。
   * 
   * @param req リクエストパラメータ
   * @param locale エラーメッセージ用
   * @param userType admin,etc
   * @throws IOException
   */
  @Transactional
  private void addCelebartion(AddCelebrationServiceAPIRequest req, Locale locale, String userType)
      throws IOException {
    UserEntity userEntity =
        userRepo.findByNoAndCommonFlagDeleteFlag(req.getUserNo(), ValueConstant.DEFAULT_DELETE_FLAG)
            .orElseThrow(() -> {
              throw AzusatoException.createI0005Error(locale, messageSource,
                  UserEntity.TABLE_NAME_KEY);
            });

    LocalDateTime nowLdt = LocalDateTime.now();

    CommonDateEntity commonDateEntity = userEntity.getCommonDate();
    commonDateEntity.setUpdateDatetime(nowLdt);

    userEntity.setName(req.getName());

    UserEntity savedUserEntity = userRepo.save(userEntity);

    CelebrationEntity insertedEntity = CelebrationEntity.builder().title(req.getTitle())
        .commonUser(CommonUserEntity
            .builder().createUserEntity(savedUserEntity).updateUserEntity(savedUserEntity).build())
        .readCount(DefaultValueConstant.READ_COUNT)
        .commonDate(
            CommonDateEntity.builder().createDatetime(nowLdt).updateDatetime(nowLdt).build())
        .commonFlag(CommonFlagEntity.builder().deleteFlag(DefaultValueConstant.DELETE_FLAG).build())
        .build();

    CelebrationEntity insetredEntity = celeRepo.save(insertedEntity);

    String contentFileName = uploadContent(req.getContent(), insetredEntity.getNo());
    insetredEntity.setContentPath(contentFileName);

    insetredEntity = celeRepo.save(insetredEntity);

    List<CelebrationNoticeEntity> notices = new ArrayList<>();

    if (userType.equals("not_admin")) {
      // 管理者リスト参照
      Set<UserEntity> admins = userRepo.findByUserTypeAndCommonFlagDeleteFlag(Type.admin.toString(),
          ValueConstant.DEFAULT_DELETE_FLAG);
      for (UserEntity admin : admins) {
        CelebrationNoticeEntity notice = CelebrationNoticeEntity.builder()
            .celebration(insertedEntity).readed(false).targetUser(admin)
            .commonDate(
                CommonDateEntity.builder().createDatetime(nowLdt).updateDatetime(nowLdt).build())
            .commonFlag(
                CommonFlagEntity.builder().deleteFlag(DefaultValueConstant.DELETE_FLAG).build())
            .build();

        notices.add(notice);

        celeNotiRepo.save(notice);
      }
    }
  }

  /**
   * お祝い修正。 お祝い番号より参照後、ない場合はエラーをスローする。 ある場合は、生成したユーザか比較し、生成したユーザではない場合はエラーをスローする。 生成したユーザの場合は更新を行う。
   * 
   * 
   * @param ModifyCelebationServiceAPIRequest 検索条件
   * @param locale エラーメッセージ用
   * @return 更新されたお祝い情報
   * @throws IOException
   * @throws AzusatoException 対象データ存在なし、生成したユーザではない場合
   */
  @Transactional
  @MethodAnnotation(description = "お祝い情報修正")
  public CelebrationEntity modifyCelebartion(ModifyCelebationServiceAPIRequest req, Locale locale)
      throws IOException {
    CelebrationEntity fetchedCelebationEntity =
        celeRepo.findByNoAndCommonFlagDeleteFlagAndCommonUserCreateUserEntityCommonFlagDeleteFlag(
            req.getCelebationNo(), ValueConstant.DEFAULT_DELETE_FLAG,
            ValueConstant.DEFAULT_DELETE_FLAG).orElseThrow(() -> {
              throw AzusatoException.createI0005Error(locale, messageSource,
                  CelebrationEntity.TABLE_NAME_KEY);
            });

    // 生成したユーザかユーザをチェックする。
    long createdUserNo = fetchedCelebationEntity.getCommonUser().getCreateUserEntity().getNo();

    if (createdUserNo != req.getUserNo()) {
      String message = messageSource.getMessage(AzusatoException.I0006, null, locale);
      throw new AzusatoException(HttpStatus.BAD_REQUEST, AzusatoException.I0006, message);
    }
    LocalDateTime now = LocalDateTime.now();
    fetchedCelebationEntity.setTitle(req.getTitle());
    fetchedCelebationEntity.setContentPath(getContentFileName(fetchedCelebationEntity.getNo()));
    fetchedCelebationEntity.getCommonUser().getCreateUserEntity().setName(req.getName());
    fetchedCelebationEntity.getCommonDate().setUpdateDatetime(now);
    fetchedCelebationEntity.getCommonUser().getCreateUserEntity().getCommonDate()
        .setUpdateDatetime(now);

    CelebrationEntity updated = celeRepo.save(fetchedCelebationEntity);

    uploadContent(req.getContent(), fetchedCelebationEntity.getNo());
    return updated;
  }

  /**
   * 「お祝い」と「お祝い書き込み」と「お祝い通知」の論理削除。 お祝い番号より参照後、ない場合はエラーをスローする。
   * ある場合は、生成したユーザか比較し、生成したユーザではない場合はエラーをスローする。 生成したユーザの場合は削除を行う。
   * 
   * 
   * @param celebationNo 削除対象のお祝い番号
   * @param userNo セッションのユーザ番号
   * @param locale エラーメッセージ用
   * @throws AzusatoException 対象データ存在なし、生成したユーザではない場合
   */
  @Transactional
  @MethodAnnotation(description = "お祝い情報削除")
  public void deleteCelebartion(Long celebationNo, Long userNo, Locale locale) {
    CelebrationEntity fetchedCelebationEntity =
        // ユーザが削除されたとしても、削除処理は行われるように
        celeRepo.findByNoAndCommonFlagDeleteFlag(celebationNo, ValueConstant.DEFAULT_DELETE_FLAG)
            .orElseThrow(() -> {
              throw AzusatoException.createI0005Error(locale, messageSource,
                  CelebrationEntity.TABLE_NAME_KEY);
            });

    // 生成したユーザかユーザをチェックする。
    long createdUserNo = fetchedCelebationEntity.getCommonUser().getCreateUserEntity().getNo();

    if (createdUserNo != userNo) {
      String message = messageSource.getMessage(AzusatoException.I0006, null, locale);
      throw new AzusatoException(HttpStatus.BAD_REQUEST, AzusatoException.I0006, message);
    }

    LocalDateTime now = LocalDateTime.now();
    fetchedCelebationEntity.getCommonFlag().setDeleteFlag(!ValueConstant.DEFAULT_DELETE_FLAG);
    fetchedCelebationEntity.getCommonDate().setUpdateDatetime(now);

    // 書き込み論理削除
    fetchedCelebationEntity.getReplys().parallelStream().forEach((e) -> {
      e.getCommonDate().setUpdateDatetime(now);
      e.getCommonFlag().setDeleteFlag(!ValueConstant.DEFAULT_DELETE_FLAG);
    });

    // お祝い通知論理削除
    fetchedCelebationEntity.getNotices().parallelStream().forEach((e) -> {
      e.getCommonDate().setUpdateDatetime(now);
      e.getCommonFlag().setDeleteFlag(!ValueConstant.DEFAULT_DELETE_FLAG);
    });

    celeRepo.save(fetchedCelebationEntity);
  }


  /**
   * 「お祝い」の参照回数をアップする。 お祝い番号より参照後、ない場合はエラーをスローする。
   * 
   * @param celebationNo 削除対象のお祝い番号
   * @param locale エラーメッセージ用
   * @return 更新されたお祝い情報
   * @throws AzusatoException 対象データ存在なし
   */
  @Transactional
  @MethodAnnotation(description = "お祝い情報の参照回数のアップ")
  public CelebrationEntity readCountUp(Long celebationNo, Locale locale) {
    CelebrationEntity fetchedCelebationEntity =
        // note：ユーザが開いた状態でのコンテンツはクリック可能にするため、deletedは使わない。
        celeRepo.findById(celebationNo).orElseThrow(() -> {
          throw AzusatoException.createI0005Error(locale, messageSource,
              CelebrationEntity.TABLE_NAME_KEY);
        });

    int upedReadCount = fetchedCelebationEntity.getReadCount() + 1;
    fetchedCelebationEntity.setReadCount(upedReadCount);

    return celeRepo.save(fetchedCelebationEntity);
  }

  /**
   * 対象のお祝いを返却する。
   * 
   * @param celebationNo 検索条件
   * @param locale エラーメッセージ用
   * @return 対象のお祝い情報
   * @throws AzusatoException 対象データが存在しない場合
   */
  @MethodAnnotation(description = "対象のお祝い情報の返却")
  public GetCelebrationSerivceAPIResponse getCelebration(Long celebationNo, Locale locale) {
    CelebrationEntity fetchedCelebationEntity = celeRepo.findById(celebationNo).orElseThrow(() -> {
      throw AzusatoException.createI0005Error(locale, messageSource,
          CelebrationEntity.TABLE_NAME_KEY);
    });

    return GetCelebrationSerivceAPIResponse.builder().celebrationNo(fetchedCelebationEntity.getNo())
        .title(fetchedCelebationEntity.getTitle()).content(fetchedCelebationEntity.getContentPath())
        .name(fetchedCelebationEntity.getCommonUser().getCreateUserEntity().getName())
        .profileImagePath(fetchedCelebationEntity.getCommonUser().getCreateUserEntity().getProfile()
            .getImagePath())
        .build();
  }

  /**
   * 対象のお祝いを返却する。
   * 
   * @param celebationNo 検索条件
   * @param userNo ログインしたユーザ番号。修正、削除権限があるかどうかチェックするため
   * @param locale エラーメッセージ用
   * @return 対象のお祝い情報
   * @throws AzusatoException テーブルにお祝いデータが存在しない。
   */
  @Transactional
  @MethodAnnotation(description = "対象のお祝い情報のコンテンツパスと書き込み情報の返却")
  public GetCelebrationContentSerivceAPIResponse getCelebrationContent(Long celebationNo,
      @NonNull Long userNo, Locale locale) {
    CelebrationEntity fetchedCelebationEntity = celeRepo.findById(celebationNo).orElseThrow(() -> {
      throw AzusatoException.createI0005Error(locale, messageSource,
          CelebrationEntity.TABLE_NAME_KEY);
    });

    return GetCelebrationContentSerivceAPIResponse.builder()
        .contentPath(fetchedCelebationEntity.getContentPath()).no(fetchedCelebationEntity.getNo())
        .owner(userNo.equals(fetchedCelebationEntity.getCommonUser().getCreateUserEntity().getNo())
            ? true
            : false)
        .replys(fetchedCelebationEntity.getReplys().stream()
            // deleted == falseだけ
            .filter((e1) -> {
              return e1.getCommonFlag().getDeleteFlag() == ValueConstant.DEFAULT_DELETE_FLAG;
            })
            // No昇順Orderby
            .sorted((e1, e2) -> Long.compare(e1.getNo(), e2.getNo())).map((e2) -> {
              CelebrationReply reply = CelebrationReply.builder().no(e2.getNo())
                  .content(e2.getContent()).createdDatetime(e2.getCommonDate().getCreateDatetime())
                  .owner(userNo.equals(
                      fetchedCelebationEntity.getCommonUser().getCreateUserEntity().getNo()) ? true
                          : false)
                  .name(e2.getCommonUser().getCreateUserEntity().getName()).profileImagePath(
                      e2.getCommonUser().getCreateUserEntity().getProfile().getImagePath())
                  .build();
              return reply;
            }).collect(Collectors.toList()))
        .build();
  }

  /**
   * お祝いリストを返却する。 お祝いNoの降順、書き込みNoの昇順で取得
   * 
   * @param req お祝いリストに関するリクエスト
   * @return お祝い+お祝い書き込みリスト
   */
  @MethodAnnotation(description = "お祝い情報リストの返却")
  public GetCelebrationsSerivceAPIResponse getCelebrations(GetCelebrationsSerivceAPIRequset req) {
    Page<CelebrationEntity> celebrationEntitys = getCelebrations(
        req.getPageReq().getCurrentPageNo() - 1, req.getPageReq().getPageOfElement());

    GetCelebrationsSerivceAPIResponse response = new GetCelebrationsSerivceAPIResponse();

    // ページング
    response.setPage(
        MyPaging.of(MyPageRequest.of(req.getPageReq(), celebrationEntitys.getTotalElements())));

    List<Celebration> celebrations = celebrationEntitys.stream().map((e) -> {
      return Celebration.builder().title(e.getTitle())
          .name(e.getCommonUser().getCreateUserEntity().getName())
          .profileImagePath(e.getCommonUser().getCreateUserEntity().getProfile().getImagePath())
          .readCount(e.getReadCount()).no(e.getNo())
          .createdDatetime(e.getCommonDate().getCreateDatetime()).build();
    }).collect(Collectors.toList());

    response.setCelebrations(celebrations);


    return response;
  }

  private Page<CelebrationEntity> getCelebrations(Integer currentPageNo, Integer pageOfElement) {
    // 注意 : 一番最初のパラメータpageは0から始まる。
    Pageable sortedByNo =
        PageRequest.of(currentPageNo, pageOfElement, Sort.by(Direction.DESC, "no"));
    return celeRepo.findAllByCommonFlagDeleteFlagAndCommonUserCreateUserEntityCommonFlagDeleteFlag(
        sortedByNo, ValueConstant.DEFAULT_DELETE_FLAG, ValueConstant.DEFAULT_DELETE_FLAG);
  }

  /**
   * お祝い番号よりページ番号を返す。 <br>
   * 削除されたお祝い情報を除いて番号を計算する <br>
   * お祝い番号の情報が削除された場合には1ページを返す。
   * 
   * @param celebrationNo お祝い番号
   * @return ページ番号
   * @throws AzusatoException celebrationNoに該当するお祝い番号が存在しない時
   */
  public Integer getPage(Long celebrationNo) {
    // 対象のお祝い情報が削除された場合には初期値を使う
    final int DEFAULT_PAGE_NO = 1;
    List<Long> celeNos = celeRepo.findAllCelebrationNos();

    Integer targetNo = null;

    // 削除されたお祝い情報を除いて番号を取得する
    for (int i = 0; i < celeNos.size(); i++) {
      if (celeNos.get(i).equals(celebrationNo)) {
        targetNo = i + 1;
        break;
      }
    }


    return Objects.isNull(targetNo) ? DEFAULT_PAGE_NO
        : (int) (Math.ceil((double) targetNo / celeProperty.getPageOfElement()));
  }

  /**
   * コンテンツを外部フォルダに格納する。
   * 
   * @param content コンテンツ
   * @param celebrationNo お祝いバン後
   * @return ファイルネーム
   * @throws IOException
   */
  private String uploadContent(InputStream content, Long celebrationNo) throws IOException {
    final String FOLDER = celeProperty.getServerContentFolderPath();
    final String FILE_NAME = getContentFileName(celebrationNo);
    final Path filePath = Paths.get(FOLDER, FILE_NAME);
    try (content;
        FileWriter fw =
            new FileWriter(filePath.toString(), Charset.forName(ValueConstant.DEFAULT_CHARSET));) {
      IOUtils.copy(content, fw, Charset.forName(ValueConstant.DEFAULT_CHARSET));
      return FILE_NAME;
    }
  }

  private String getContentFileName(Long celebrationNo) {
    return String.valueOf(celebrationNo) + "." + celeProperty.getContentExtention();
  }
}
