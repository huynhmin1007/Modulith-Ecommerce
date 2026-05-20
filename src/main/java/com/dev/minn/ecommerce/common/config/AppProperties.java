package com.dev.minn.ecommerce.common.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app")
@Getter
@Setter
public class AppProperties {

    private Security security;

    @Getter
    @Setter
    public static class Security {
        private String[] publicEndpoints;
    }
}