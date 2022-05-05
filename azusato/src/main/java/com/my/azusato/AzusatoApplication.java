package com.my.azusato;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import com.my.azusato.property.ProfileProperty;
import com.my.azusato.property.SessionProperty;
import com.my.azusato.property.UserProperty;

@SpringBootApplication
// for using @ConfigurationProperties
@EnableConfigurationProperties(value = { UserProperty.class, ProfileProperty.class, SessionProperty.class })
public class AzusatoApplication {

	public static void main(String[] args) {
		SpringApplication.run(AzusatoApplication.class, args);
	}

}
