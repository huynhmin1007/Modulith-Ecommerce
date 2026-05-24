package com.dev.minn.ecommerce.notification.application.exception;

import com.dev.minn.ecommerce.common.application.exception.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum NotificationErrorCode implements BaseErrorCode {

    TEMPLATE_NOT_FOUND(1001, "Template not found", HttpStatus.NOT_FOUND),
    FAILED_TO_SEND_NOTIFICATION(1002, "Failed to send notification", HttpStatus.INTERNAL_SERVER_ERROR),;

    private int code;
    private String message;
    private HttpStatus status;
}
