package com.dev.minn.ecommerce.identity.service.impl.jwt;

import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app.jwt")
@Setter
public class JwtProperties {

    public long accessValidity;
    public long refreshValidity;
    public String issuer;
}
