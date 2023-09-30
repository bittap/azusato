package com.my.azusato.provider;

import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * {@link Bean}に登録されいないクラスでSpring {@link Bean}を使うため。
 * 
 * @author Carmel
 *
 */
@Component
public class ApplicationContextProvider implements ApplicationContextAware {
  private static ApplicationContext context;

  public static ApplicationContext getApplicationContext() {
    return context;
  }

  @Override
  public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    context = applicationContext;
  }

  public static HttpServletRequest getCurrentHttpRequest() {
    RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();

    HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
    return request;
  }
}
