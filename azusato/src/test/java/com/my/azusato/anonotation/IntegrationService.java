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
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

/**
 * Service~Repositoryまでの結合テスト用のアノテーション
 * 
 * <p>
 * テスト対象のクラスは{@link Import}で独自に宣言し、Importする必要があります。
 * <p>
 * * 理由:{@link @ataJpaTest#value()}ではどのコントローラーをImportするか判断ができないため。
 * <p>
 * Ex)
 * 
 * <pre>
 * &#64;ActiveProfiles(value = "test")
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
 * </pre>
 * 
 * @author Carmel
 *
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@ActiveProfiles(value = "test")
@ImportAutoConfiguration(value = MessageSourceAutoConfiguration.class)
public @interface IntegrationService {

}
