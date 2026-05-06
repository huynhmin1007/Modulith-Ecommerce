package com.dev.minn.ecommerce.shared.exception;

import org.springframework.http.HttpStatus;

public interface BaseErrorCode {
    int getCode();
    String getMessage();
    HttpStatus getStatus();
}
