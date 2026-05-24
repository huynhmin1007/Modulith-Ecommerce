package com.dev.minn.ecommerce.notification.application.listener;

import com.dev.minn.ecommerce.common.application.service.EventIdempotencyService;
import com.dev.minn.ecommerce.common.domain.EventEnvelope;
import com.dev.minn.ecommerce.identity.event.UserRegisteredEvent;
import com.dev.minn.ecommerce.identity.event.UserRegistrationInitiatedEvent;
import com.dev.minn.ecommerce.notification.application.dto.NotificationRequest;
import com.dev.minn.ecommerce.notification.application.service.NotificationService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NotificationEventListener {

    private static final String CONSUMER = "NotificationEventListener";
    EventIdempotencyService idempotency;
    NotificationService notificationService;

    @ApplicationModuleListener
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void onUserRegistrationInitiated(EventEnvelope<UserRegistrationInitiatedEvent> event) {
        log.info("Event received: {}", event);

        boolean marked = idempotency.markedConsumed(event.eventId(), CONSUMER);
        if (!marked) {
            log.info("Event {} already processed", event.eventId());
            return;
        }

        UserRegistrationInitiatedEvent payload = event.payload();
        throw new RuntimeException("Test failed");
//        notificationService.send(
//                NotificationRequest.builder()
//                        .channel("email")
//                        .recipient(payload.email())
//                        .templateCode("user:registration:verify-otp")
//                        .variables(
//                                Map.of(
//                                        "username",
//                                        payload.username(),
//
//                                        "otp",
//                                        payload.otp()
//                                )
//                        )
//                        .referenceType("user:registration:verify-otp")
//                        .referenceId(payload.email())
//                        .build()
//        );
    }

    @ApplicationModuleListener
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void onUserRegistered(EventEnvelope<UserRegisteredEvent> event) {
        log.info("Event received: {}", event);

        boolean marked = idempotency.markedConsumed(event.eventId(), CONSUMER);
        if (!marked) {
            log.info("Event {} already processed", event.eventId());
            return;
        }

        UserRegisteredEvent payload = event.payload();
        notificationService.send(NotificationRequest.builder()
                .channel("email")
                .recipient(payload.email())
                .templateCode("user:registration:welcome")
                .variables(Map.of("username", payload.username()))
                .referenceType("user:registration:welcome")
                .referenceId(event.correlationId())
                .build());
    }
}
