package com.my.azusato.anonotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.context.MessageSourceAutoConfiguration;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.stereotype.Service;
import org.springframework.test.context.ActiveProfiles;
import com.my.azusato.common.TestConstant;

/**
 * Service~Repositoryまでの結合テスト用のアノテーション
 * 
 * Ex)
 * 
 * <pre>
 * &#64;IntegrationService
 * public class UserServiceAPITest {
 * }
 * </pre>
 * 
 * 以下のサポートする。
 * 
 * <pre>
 * 1.{@link DataJpaTest}によるJpaサポート
 * 2.application-test.ymlに設定
 * 3.メモリDBではなく、「application-test.yml」に登録したDBを使う。
 * 4.MessageSourceをimportする
 * 5.Serviceを全部インポート
 * </pre>
 * 
 * @author Carmel
 *
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@DataJpaTest(
    includeFilters = {@ComponentScan.Filter(type = FilterType.ANNOTATION, value = {Service.class})})
@AutoConfigureTestDatabase(replace = Replace.NONE)
@ActiveProfiles(value = TestConstant.PROFILES)
@ImportAutoConfiguration(value = MessageSourceAutoConfiguration.class)
public @interface IntegrationService {

}
