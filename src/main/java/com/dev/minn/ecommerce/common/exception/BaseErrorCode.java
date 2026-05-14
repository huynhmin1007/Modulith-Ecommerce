package com.dev.minn.ecommerce.common.exception;

import org.springframework.http.HttpStatus;

public interface BaseErrorCode {
    int getCode();
    String getMessage();
    HttpStatus getStatus();
}
