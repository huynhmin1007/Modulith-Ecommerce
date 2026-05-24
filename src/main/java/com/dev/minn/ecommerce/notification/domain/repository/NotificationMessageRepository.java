package com.dev.minn.ecommerce.notification.domain.repository;

import com.dev.minn.ecommerce.notification.domain.model.NotificationMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface NotificationMessageRepository extends JpaRepository<NotificationMessage, UUID> {
}
