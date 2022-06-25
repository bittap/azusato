package com.my.azusato.aop;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;

import com.my.azusato.annotation.MethodAnnotation;

public class AOPUtil {

	
	/**
	 * アノテーション{@link MethodAnnotation}から説明を取得。
	 * @param joinPoint　実行される対象API
	 * @return APIネーム
	 */
	public static String getDescription(JoinPoint joinPoint) {
		MethodSignature signature = (MethodSignature) joinPoint.getSignature();
	    Method method = signature.getMethod();

	    MethodAnnotation apiAnno = method.getAnnotation(MethodAnnotation.class);
	    
	    return apiAnno.description();
	}
	
	public static String getParameterMsg(Object[] parameterValues) {
		// map -> String
		return Arrays.asList(parameterValues).stream()
				.filter(Objects::nonNull)
				.map((e) -> String.format("%s : %s", e.getClass().getSimpleName(), e)).collect(Collectors.joining("、"));
	}
	
	/**
	 * 戻り値がある場合はその戻り値のtoString。その以外は"void"を返却する。
	 * @param returnValue 戻り値
	 * @return 戻り値がある場合 : toString, その以外 : void
	 */
	public static String getReturnValue(Object returnValue) {
		return Objects.isNull(returnValue) ? "void" : returnValue.toString();
	}
}
