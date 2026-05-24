package com.dev.minn.ecommerce.notification.domain.repository;

import com.dev.minn.ecommerce.notification.domain.model.NotificationTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface NotificationTemplateRepository extends JpaRepository<NotificationTemplate, UUID> {
    Optional<NotificationTemplate> findByCode(String code);
    boolean existsByCode(String code);
}
