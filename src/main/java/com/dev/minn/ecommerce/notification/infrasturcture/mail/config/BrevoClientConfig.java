package com.dev.minn.ecommerce.notification.infrasturcture.mail.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
@RequiredArgsConstructor
public class BrevoClientConfig {

    private final BrevoProperties properties;

    @Bean
    RestClient brevoRestClient() {

        return RestClient.builder()
                .baseUrl(properties.baseUrl())
                .defaultHeader(
                        "api-key",
                        properties.apiKey()
                )
                .defaultHeader(
                        "accept",
                        "application/json"
                )
                .build();
    }
}