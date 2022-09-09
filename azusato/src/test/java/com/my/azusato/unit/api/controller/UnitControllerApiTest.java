package com.my.azusato.unit.api.controller;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.servlet.HandlerInterceptor;
import com.my.azusato.config.SecurityConfig;
import com.my.azusato.config.WebMvcConfig;

/**
 * APIコントローラーテストの共通アノテーションを宣言。
 * <p>
 * 除外するクラス・アノテーション
 * 
 * <pre>
 * ・SpringSecurity
 * ・Intercepter
 * ・Component
 * ・Service
 * ・Repository
 * </pre>
 * 
 * @author Carmel
 *
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@ExtendWith(SpringExtension.class)
@WebMvcTest(excludeFilters = {@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
    classes = {SecurityConfig.class, WebMvcConfig.class, HandlerInterceptor.class})})
public @interface UnitControllerApiTest {

}
