package com.dev.minn.ecommerce;

import com.dev.minn.ecommerce.identity.config.RsaKeyProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import java.util.TimeZone;

@SpringBootApplication
@EnableConfigurationProperties(RsaKeyProperties.class)
public class ModulithEcommerceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ModulithEcommerceApplication.class, args);
	}

	static {
		TimeZone.setDefault(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
	}
}
