package com.dev.minn.ecommerce.identity.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

@ConfigurationProperties(prefix = "app.rsa")
public record RsaKeyProperties(
        RSAPublicKey publicKey,
        RSAPrivateKey privateKey
) {
}
