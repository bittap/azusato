package com.my.azusato.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Aspect
@Component
@Slf4j
public class ServiceAPILoggingAOP {
	
	/**
	 * 一番先のタイトル
	 */
	public static final String FULL_TITLE = "ビジネスロジック"; 
	
	@Before(value = "execution (* com.my.azusato.api.service.*.*(..)) && @annotation(com.my.azusato.annotation.MethodAnnotation)")
	public void beforeAPI(JoinPoint joinPoint) {
		String parameterMsg = AOPUtil.getParameterMsg(joinPoint.getArgs());
		log.debug("【{}】[{}]処理開始\n[パラメータ] : {}", FULL_TITLE,AOPUtil.getDescription(joinPoint), parameterMsg);
	}

	@AfterReturning(value = "execution (* com.my.azusato.api.service.*.*(..)) && @annotation(com.my.azusato.annotation.MethodAnnotation)", returning = "returnValue")
	public void afterReturnAPI(JoinPoint joinPoint, Object returnValue) {
		log.debug("【{}】[{}]処理成功\n[結果] : {}", FULL_TITLE,AOPUtil.getDescription(joinPoint), AOPUtil.getReturnValue(returnValue));
	}
}
