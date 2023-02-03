package com.my.azusato.filter;

import java.io.IOException;
import java.security.Principal;
import java.util.Objects;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import org.jboss.logging.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

/**
 * ログ出力時ユーザを識別するためにMDCを登録するクラス。
 * 
 * @author Carmel
 *
 */
@Component
@Slf4j
@Order(Integer.MIN_VALUE) // 一番前に実行されるようにする。
public class MDCFilter implements Filter {

  /**
   * MDCの{@code {USER_ID}}のキー
   */
  public final String USER_ID_KEY = "USER_ID";

  /**
   * MDCを登録する。スレッドの処理が終わったらMDCは初期化する。
   */
  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {
    log.debug("MDC Filter START::::::::::::");
    HttpServletRequest req = (HttpServletRequest) request;
    String userId = getUserId(req);
    registerUserId(userId);

    try {
      chain.doFilter(request, response);
    } finally {
      MDC.clear();
      log.debug("MDC Filter END::::::::::::");
    }
  }

  /**
   * ユーザIDを取得する。
   * 
   * @param req リクエスト
   * @return ログイン情報がない場合 : null , その以外 : ユーザID
   */
  private String getUserId(HttpServletRequest req) {
    Principal principal = req.getUserPrincipal();
    return Objects.isNull(principal) ? null : principal.getName();
  }

  /**
   * userIdがnull以外の場合はMDCに登録する。
   * 
   * @param userId ユーザID
   */
  private void registerUserId(String userId) {
    if (Objects.nonNull(userId))
      MDC.put(USER_ID_KEY, userId);
  }

}
