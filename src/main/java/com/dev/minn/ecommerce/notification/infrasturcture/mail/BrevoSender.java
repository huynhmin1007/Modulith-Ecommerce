package com.dev.minn.ecommerce.notification.infrasturcture.mail;

import com.dev.minn.ecommerce.notification.infrasturcture.mail.config.BrevoProperties;
import com.dev.minn.ecommerce.notification.infrasturcture.mail.dto.BrevoSendMailRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BrevoSender {

    @Qualifier("brevoRestClient")
    RestClient brevoRestClient;
    BrevoProperties properties;

    public void send(String recipient, String subject, String htmlContent) {
        var request = BrevoSendMailRequest.builder()
                .sender(new BrevoSendMailRequest.Sender(
                        properties.senderName(),
                        properties.senderEmail()
                ))
                .to(List.of(new BrevoSendMailRequest.Recipient(recipient)))
                .subject(subject)
                .htmlContent(htmlContent)
                .build();

        brevoRestClient.post()
                .uri("/smtp/email")
                .body(request)
                .retrieve()
                .toBodilessEntity();
    }
}
