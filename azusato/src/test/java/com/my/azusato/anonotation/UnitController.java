package com.my.azusato.anonotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.stereotype.Controller;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.servlet.HandlerInterceptor;
import com.my.azusato.config.WebMvcConfig;

/**
 * コントローラーUnitテストの共通アノテーション
 * 
 * <p>
 * テスト対象のクラスは{@link Import}で独自に宣言し、Importする必要があります。
 * <p>
 * * 理由:{@link WebMvcTest#value()}ではどのコントローラーをImportするか判断ができないため。
 * <p>
 * Ex)
 * 
 * <pre>
 * &#64;UnitController
 * &#64;Import(value = CelebrationControllerAPI.class)
 * class CelebrationControllerApiTest {
 * }
 * </pre>
 * 
 * 以下のサポートする。
 * 
 * <pre>
 * 1.{@link WebMvcTest}によるコントローラーのUnitテストをサポート
 * 2.application-test.ymlに設定
 * 3.Interceptorは除外
 * </pre>
 * 
 * @author Carmel
 *
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(value = InMemoryUserDetailsManager.class) // Spring-SecurityでDIする時エラーになるため、追加
@WebMvcTest(excludeFilters = {
    // Interceptor除外
    @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
        classes = {WebMvcConfig.class, HandlerInterceptor.class}),
    // valueにimportするコントローラークラスを共通アノテーションでは指定できないため、コントローラーは全部除外する。
    // コントローラーを追加する方法は上記のコメントの例を参考
    @ComponentScan.Filter(type = FilterType.ANNOTATION, value = {Controller.class})})
@ActiveProfiles("test") // application-test.xmlを使う。
public @interface UnitController {

}
