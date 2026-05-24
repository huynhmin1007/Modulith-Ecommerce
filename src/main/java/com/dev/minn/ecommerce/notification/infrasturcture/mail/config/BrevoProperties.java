package com.dev.minn.ecommerce.notification.infrasturcture.mail.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.brevo")
public record BrevoProperties(
        String apiKey,
        String baseUrl,
        String senderName,
        String senderEmail
) {
}
