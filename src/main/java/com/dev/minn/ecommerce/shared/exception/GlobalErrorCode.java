package com.dev.minn.ecommerce.shared.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum GlobalErrorCode implements BaseErrorCode {

    INTERNAL_ERROR(5000, "Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_REQUEST(4000, "Invalid Request Parameters", HttpStatus.BAD_REQUEST);

    private final int code;
    private final String message;
    private final HttpStatus status;
}
