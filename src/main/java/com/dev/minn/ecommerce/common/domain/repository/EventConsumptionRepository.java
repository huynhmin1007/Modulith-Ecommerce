package com.dev.minn.ecommerce.common.domain.repository;

import com.dev.minn.ecommerce.common.domain.entity.EventConsumption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface EventConsumptionRepository extends JpaRepository<EventConsumption, UUID> {

    boolean existsByEventIdAndConsumer(String eventId, String consumer);
    void deleteByEventIdAndConsumer(String eventId, String consumer);
}
