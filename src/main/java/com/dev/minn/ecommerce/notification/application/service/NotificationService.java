package com.dev.minn.ecommerce.notification.application.service;

import com.dev.minn.ecommerce.notification.application.dto.NotificationRequest;

public interface NotificationService {
    void send(NotificationRequest request);
}
