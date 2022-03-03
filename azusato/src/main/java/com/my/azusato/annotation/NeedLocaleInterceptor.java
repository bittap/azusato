package com.my.azusato.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.my.azusato.interceptor.LocaleInterCeptor;
import com.my.azusato.view.controller.response.HeaderReponse;

/**
 * This annotation announce adding Locale in {@link LocaleInterCeptor}. Therefore Locale is added to {@link HeaderReponse}
 * target is method of controller.   
 * @author Carmel
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface NeedLocaleInterceptor{
	
}
