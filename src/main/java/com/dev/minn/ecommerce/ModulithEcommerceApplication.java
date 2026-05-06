package com.dev.minn.ecommerce;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.TimeZone;

@SpringBootApplication
public class ModulithEcommerceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ModulithEcommerceApplication.class, args);
	}

	static {
		TimeZone.setDefault(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
	}
}
