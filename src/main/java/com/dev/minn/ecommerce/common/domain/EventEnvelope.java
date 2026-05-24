package com.dev.minn.ecommerce.common.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.core.ResolvableType;
import org.springframework.core.ResolvableTypeProvider;

import java.time.Instant;
import java.util.UUID;

public record EventEnvelope<T>(
        String eventId,
        String eventType,
        String correlationId,
        String producer,
        Instant occurredAt,
        T payload
) implements ResolvableTypeProvider {
    public static <T> EventEnvelope<T> of(
            String eventType, String correlationId, String producer, T payload
    ) {
        return new EventEnvelope<>(
                UUID.randomUUID().toString(),
                eventType,
                correlationId,
                producer,
                Instant.now(),
                payload
        );
    }

    @JsonIgnore
    @Override
    public ResolvableType getResolvableType() {

        return ResolvableType.forClassWithGenerics(
                getClass(),
                ResolvableType.forInstance(payload)
        );
    }
}
