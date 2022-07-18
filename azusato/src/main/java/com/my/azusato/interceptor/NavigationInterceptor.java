package com.my.azusato.interceptor;

import java.util.Objects;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.my.azusato.api.controller.request.MyPageControllerRequest;
import com.my.azusato.api.service.CelebrationNoticeServiceAPI;
import com.my.azusato.api.service.request.GetCelebrationsSerivceAPIRequset;
import com.my.azusato.api.service.response.GetCelebrationNoticesSerivceAPIResponse;
import com.my.azusato.login.LoginUser;
import com.my.azusato.util.LoginUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * ログに関する。interceptor
 * 
 * @author kim-t
 *
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class NavigationInterceptor implements HandlerInterceptor {
	
	public static final String MODEL_MAP_NAME = "nav";
	
	private final CelebrationNoticeServiceAPI celeNotiAPIService;

	/**
	 * modelをログする。
	 */
	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		if(Objects.nonNull(modelAndView) && Objects.nonNull(modelAndView.getModel())) {
			if(Objects.nonNull(request.getUserPrincipal())) {
				MyPageControllerRequest page = new MyPageControllerRequest(1, 3, 5);
				
				GetCelebrationsSerivceAPIRequset serviceReq = GetCelebrationsSerivceAPIRequset.builder()
										.pageReq(page)
										.build();
				LoginUser loginUser = LoginUtil.getLoginUser();
				GetCelebrationNoticesSerivceAPIResponse result = celeNotiAPIService.celebrationNotices(serviceReq,loginUser.getUSER_NO());
				modelAndView.addObject(MODEL_MAP_NAME, result);
				log.debug("お祝い通知(nav):対象:{}",result);
			}else {
				log.debug("お祝い通知(nav):ログインしていないため非対象");
			}
			
		}else {
			log.debug("お祝い通知(nav):modelAndViewがないため非対象");
		}
		
	}
}
