package com.my.azusato.aop;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;

import com.my.azusato.annotation.MethodAnnotation;

import lombok.extern.slf4j.Slf4j;

/**
 * AOPに関するUtilクラス。
 * @author Carmel
 *
 */
@Slf4j
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
	
	/**
	 * パラメータネームと値を結合した文字列を取得する。
	 * パラメータネーム1:値、パラメータネーム2:値...
	 * @param parameterNames パラメータネーム
	 * @param parameterValues パラメータ値
	 * @return 結合した文字列
	 */
	public static String getParameterMsg(String[] parameterNames , Object[] parameterValues) {
		if(parameterNames.length != parameterValues.length) {
			log.warn("パラメータ配列のネームと値の数が異なります。 ネーム数 : {} , 値の数 : {}", parameterNames.length, parameterValues.length);
		}
		
		Map<String, Object> parameters = new HashMap<String, Object>();
		for (int i = 0; i < parameterNames.length; i++) {
			parameters.put(parameterNames[i], parameterValues[i]);
		}
		
		return parameters.keySet().stream().map((e)->{
			String value = Objects.nonNull(parameters.get(e)) ? parameters.get(e).toString() : "";
			return String.format("%s : %s", e, value);
		}).collect(Collectors.joining("、"));
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
