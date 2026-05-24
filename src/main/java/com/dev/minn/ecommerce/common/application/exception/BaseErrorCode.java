package com.dev.minn.ecommerce.common.application.exception;

import org.springframework.http.HttpStatus;

public interface BaseErrorCode {
    int getCode();
    String getMessage();
    HttpStatus getStatus();

    default BusinessException throwException() {
        return new BusinessException(this);
    }
}
