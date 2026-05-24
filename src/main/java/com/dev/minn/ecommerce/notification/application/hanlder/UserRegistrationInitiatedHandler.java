package com.dev.minn.ecommerce.notification.application.hanlder;

import com.dev.minn.ecommerce.common.domain.EventEnvelope;
import com.dev.minn.ecommerce.common.domain.EventHandler;
import com.dev.minn.ecommerce.identity.event.UserRegistrationInitiatedEvent;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserRegistrationInitiatedHandler implements EventHandler<UserRegistrationInitiatedEvent> {

    @Override
    public Class<UserRegistrationInitiatedEvent> supports() {
        return UserRegistrationInitiatedEvent.class;
    }

    @Override
    public void handle(EventEnvelope<UserRegistrationInitiatedEvent> event) {

    }
}
