package com.my.azusato;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import com.my.azusato.property.CelebrationNoticeProperty;
import com.my.azusato.property.CelebrationProperty;
import com.my.azusato.property.CookieProperty;
import com.my.azusato.property.ProfileProperty;
import com.my.azusato.property.RestProperty;
import com.my.azusato.property.SessionProperty;
import com.my.azusato.property.UserProperty;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

@SpringBootApplication
// for using @ConfigurationProperties
@EnableConfigurationProperties(
    value = {UserProperty.class, ProfileProperty.class, SessionProperty.class, RestProperty.class,
        CookieProperty.class, CelebrationProperty.class, CelebrationNoticeProperty.class})
@OpenAPIDefinition(info = @Info(title = "あずさとAPI", //
    version = "v1")) //
public class AzusatoApplication {

  public static void main(String[] args) {
    SpringApplication.run(AzusatoApplication.class, args);
  }

}
