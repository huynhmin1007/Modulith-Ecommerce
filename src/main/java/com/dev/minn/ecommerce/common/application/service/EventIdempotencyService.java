package com.dev.minn.ecommerce.common.application.service;

import com.dev.minn.ecommerce.common.domain.entity.EventConsumption;
import com.dev.minn.ecommerce.common.domain.repository.EventConsumptionRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Transactional(readOnly = true)
public class EventIdempotencyService {

    EventConsumptionRepository repository;

    @Transactional
    public boolean markedConsumed(String eventId, String consumer) {
        try {
            repository.save(EventConsumption.builder()
                    .eventId(eventId)
                    .consumer(consumer)
                    .build());

            return true;
        } catch (DataIntegrityViolationException e) {
            return false;
        }
    }

    @Transactional
    public void delete(String eventId, String consumer) {
        repository.deleteByEventIdAndConsumer(eventId, consumer);
    }
}
