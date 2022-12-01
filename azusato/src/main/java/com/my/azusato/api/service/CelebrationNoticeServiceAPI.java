package com.my.azusato.api.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import javax.transaction.Transactional;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.stereotype.Service;
import com.my.azusato.annotation.MethodAnnotation;
import com.my.azusato.api.service.request.GetCelebrationsSerivceAPIRequset;
import com.my.azusato.api.service.response.GetCelebrationNoticesSerivceAPIResponse;
import com.my.azusato.entity.CelebrationNoticeEntity;
import com.my.azusato.exception.AzusatoException;
import com.my.azusato.repository.CelebrationNoticeRepository;
import com.my.azusato.view.controller.common.ValueConstant;
import lombok.RequiredArgsConstructor;

/**
 * お祝い通知に関するサービスクラス。 以下のサービスメソッドがある。
 * 
 * <pre>
 *  ①お祝い通知情報リストの返却
 *  ②お祝い通知の既読処理
 * </pre>
 * 
 * @author Carmel
 *
 */
@Service
@RequiredArgsConstructor
public class CelebrationNoticeServiceAPI {

  private final CelebrationNoticeRepository celeNotiRepo;

  private final MessageSource messageSource;

  /**
   * "loginUserNo"に紐づくお祝いリストを返却する。 書き込み番号があると、書き込み情報からタイトル等の情報を取得する。書き込み番号がないと、お祝い情報からタイトル等の情報を取得する。
   * ソート順：参照フラグ昇順、お祝いNoの降順、書き込みNoの降順で取得
   * 
   * @param req お祝いリストに関するリクエスト
   * @param loginUserNo ログインしたユーザ番号
   * @return お祝い通知情報リスト
   */
  @MethodAnnotation(description = "お祝い通知情報リストの返却")
  @Transactional
  public GetCelebrationNoticesSerivceAPIResponse celebrationNotices(
      GetCelebrationsSerivceAPIRequset req, Long loginUserNo) {
    Page<CelebrationNoticeEntity> celebrationNotices = getCelebrationNotices(
        req.getPageReq().getCurrentPageNo() - 1, req.getPageReq().getPageOfElement(), loginUserNo);


    GetCelebrationNoticesSerivceAPIResponse response =
        new GetCelebrationNoticesSerivceAPIResponse();
    List<GetCelebrationNoticesSerivceAPIResponse.Notice> notices = new ArrayList<>();

    int noReadLength = 0;
    for (CelebrationNoticeEntity celebrationNotice : celebrationNotices) {
      if (celebrationNotice.getReaded() == false) {
        noReadLength++;
      }
      String name;
      String title;
      LocalDateTime createdDatetime;
      Long celeReplyNo = null;
      String profileImagePath;
      if (Objects.nonNull(celebrationNotice.getReply())) {
        name = celebrationNotice.getReply().getCommonUser().getCreateUserEntity().getName();
        title = celebrationNotice.getReply().getContent();
        createdDatetime = celebrationNotice.getReply().getCommonDate().getCreateDatetime();
        celeReplyNo = celebrationNotice.getReply().getNo();
        profileImagePath = celebrationNotice.getReply().getCommonUser().getCreateUserEntity()
            .getProfile().getImagePath();
      } else {
        name = celebrationNotice.getCelebration().getCommonUser().getCreateUserEntity().getName();
        title = celebrationNotice.getCelebration().getTitle();
        createdDatetime = celebrationNotice.getCelebration().getCommonDate().getCreateDatetime();
        profileImagePath = celebrationNotice.getCelebration().getCommonUser().getCreateUserEntity()
            .getProfile().getImagePath();
      }
      GetCelebrationNoticesSerivceAPIResponse.Notice notice =
          GetCelebrationNoticesSerivceAPIResponse.Notice.builder().no(celebrationNotice.getNo())
              .name(name).title(title).profileImagePath(profileImagePath)
              .createdDatetime(createdDatetime)
              .celebrationNo(celebrationNotice.getCelebration().getNo())
              .celebrationReplyNo(celeReplyNo).readed(celebrationNotice.getReaded()).build();

      notices.add(notice);
    }

    response.setNotices(notices);
    response.setNoReadLength(noReadLength);

    return response;
  }

  /**
   * お祝い通知リストを返却する。 ソート順：参照フラグ昇順、お祝いNoの降順、書き込みNoの降順で取得
   * 
   * @param currentPageNo 現在ページ番号
   * @param pageOfElement ページのリスト表示数
   * @param loginUserNo ユーザ番号
   * @return pageOfElement数のお祝い通知リスト
   */
  private Page<CelebrationNoticeEntity> getCelebrationNotices(Integer currentPageNo,
      Integer pageOfElement, Long loginUserNo) {
    // 注意 : 一番最初のパラメータpageは0から始まる。
    List<Order> orders =
        List.of(Order.asc("readed"), Order.desc("celebration_no"), Order.desc("reply.no"));
    Pageable sortedByNo = PageRequest.of(currentPageNo, pageOfElement, Sort.by(orders));
    return celeNotiRepo.findAllByTargetUserNoAndCommonFlagDeleteFlag(sortedByNo, loginUserNo,
        ValueConstant.DEFAULT_DELETE_FLAG);
  }

  /**
   * お祝い通知の"既読フラグ"をtrueに変更する。"celebationNo"と"loginUserNo"に一致する通知リストを返却し、"既読フラグ"をtrueに変更する。
   * "celebationNo"と"loginUserNo"に一致する通知リストが一つも存在しない場合は、400エラーをスローする。
   * 
   * @param celebationNo お祝い番号
   * @param loginUserNo ログインしたユーザの情報
   * @param locale エラーメッセージ用
   */
  @Transactional
  @MethodAnnotation(description = "お祝い通知の既読処理")
  public void read(Long celebationNo, long loginUserNo, Locale locale) {
    List<CelebrationNoticeEntity> notices =
        celeNotiRepo.findAllByCelebrationNoAndTargetUserNo(celebationNo, loginUserNo);

    if (notices.size() == 0)
      throw AzusatoException.createI0005Error(locale, messageSource,
          CelebrationNoticeEntity.TABLE_NAME_KEY);

    LocalDateTime now = LocalDateTime.now();
    for (CelebrationNoticeEntity notice : notices) {
      notice.setReaded(true);
      notice.setReadDatetime(now);
      notice.getCommonDate().setUpdateDatetime(now);

      celeNotiRepo.save(notice);
    }
  }

}
