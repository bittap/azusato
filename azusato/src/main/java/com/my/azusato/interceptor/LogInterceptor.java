package com.my.azusato.interceptor;

import java.util.Map;
import java.util.Objects;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

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
public class LogInterceptor implements HandlerInterceptor {

	/**
	 * modelをログする。
	 */
	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		if(Objects.nonNull(modelAndView) && Objects.nonNull(modelAndView.getModel())) {
			Map<String, Object> models = modelAndView.getModel();
			
			models.forEach((k,v)->{
				log.debug("[postHandle][model map]key : {} , value : {}",k,v);
			});
		}
		
	}
}
