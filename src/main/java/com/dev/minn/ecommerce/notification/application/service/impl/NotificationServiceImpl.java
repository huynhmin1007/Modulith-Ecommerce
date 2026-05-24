package com.dev.minn.ecommerce.notification.application.service.impl;

import com.dev.minn.ecommerce.common.application.exception.BusinessException;
import com.dev.minn.ecommerce.notification.application.dto.NotificationRequest;
import com.dev.minn.ecommerce.notification.application.exception.NotificationErrorCode;
import com.dev.minn.ecommerce.notification.application.service.NotificationService;
import com.dev.minn.ecommerce.notification.domain.model.NotificationMessage;
import com.dev.minn.ecommerce.notification.domain.model.NotificationTemplate;
import com.dev.minn.ecommerce.notification.domain.repository.NotificationMessageRepository;
import com.dev.minn.ecommerce.notification.domain.repository.NotificationTemplateRepository;
import com.dev.minn.ecommerce.notification.infrasturcture.mail.BrevoSender;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Transactional(readOnly = true)
public class NotificationServiceImpl implements NotificationService {

    private static final Pattern PATTERN = Pattern.compile("\\{\\{(.*?)}}");

    NotificationTemplateRepository templateRepository;
    NotificationMessageRepository messageRepository;
    BrevoSender sender;

    @Transactional
    @Override
    public void send(NotificationRequest request) {
        NotificationTemplate template = templateRepository.findByCode(request.getTemplateCode())
                .orElseThrow(NotificationErrorCode.TEMPLATE_NOT_FOUND::throwException);

        String subject = render(template.getSubjectTemplate(), request.getVariables());
        String content = render(template.getBodyTemplate(), request.getVariables());

        NotificationMessage message = NotificationMessage.builder()
                .template(template)
                .channel(request.getChannel())
                .recipient(request.getRecipient())
                .subject(subject)
                .content(content)
                .variables(request.getVariables())
                .referenceType(request.getReferenceType())
                .referenceId(request.getReferenceId())
                .status("PENDING")
                .build();

        try {
            sender.send(
                    request.getRecipient(),
                    subject,
                    content
            );

            message.setStatus("SENT");
            message.setSentAt(Instant.now());
        } catch (Exception e) {
            message.setStatus("FAILED");
            log.error("Failed to send notification", e);
            throw new BusinessException(NotificationErrorCode.FAILED_TO_SEND_NOTIFICATION);
        }
        messageRepository.save(message);
    }

    public String render(String template, Map<String, Object> variables) {
        Matcher matcher = PATTERN.matcher(template);
        StringBuffer buffer = new StringBuffer();

        while (matcher.find()) {
            String key = matcher.group(1).trim();

            Object value = variables.getOrDefault(key, "");

            matcher.appendReplacement(
                    buffer,
                    Matcher.quoteReplacement(String.valueOf(value))
            );
        }

        matcher.appendTail(buffer);

        return buffer.toString();
    }
}
