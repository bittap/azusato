package com.my.azusato.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * メソッドアノテーション。
 * 現在はAOPでログ記録用のみ使われている。
 * @author Carmel
 *
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MethodAnnotation {

	/**
	 * 説明内容。
	 * ログ記録のため使われる。
	 */
	String description() default "";
}
