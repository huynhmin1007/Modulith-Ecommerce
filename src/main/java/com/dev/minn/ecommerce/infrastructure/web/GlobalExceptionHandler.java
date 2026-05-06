package com.dev.minn.ecommerce.infrastructure.web;

import com.dev.minn.ecommerce.shared.api.ApiResponse;
import com.dev.minn.ecommerce.shared.exception.BaseErrorCode;
import com.dev.minn.ecommerce.shared.exception.BusinessException;
import com.dev.minn.ecommerce.shared.exception.GlobalErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RestControllerAdvice(basePackages = "com.dev.minn")
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleAppException(BusinessException e) {
        BaseErrorCode code = e.getErrorCode();
        log.warn("AppException: {} - {}", code.getCode(), code.getMessage());

        return ResponseEntity
                .status(code.getStatus())
                .body(ApiResponse.<Void>builder()
                        .code(code.getCode())
                        .message(code.getMessage())
                        .traceId(UUID.randomUUID().toString())
                        .build());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidation(MethodArgumentNotValidException e) {
        Map<String, String> errors = new HashMap<>();

        for (FieldError error : e.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }

        log.warn("Validation Error: {}", errors);

        return ResponseEntity
                .badRequest()
                .body(ApiResponse.<Map<String, String>>builder()
                        .code(GlobalErrorCode.INVALID_REQUEST.getCode())
                        .message(GlobalErrorCode.INVALID_REQUEST.getMessage())
                        .data(errors)
                        .traceId(UUID.randomUUID().toString())
                        .build()
                );
    }

    // 3. Bắt các lỗi vỡ hệ thống (NullPointer, đứt mạng...)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleUnexpected(Exception e) {
        log.error("Unhandled Exception: ", e);

        return ResponseEntity
                .internalServerError()
                .body(ApiResponse.<Void>builder()
                        .code(GlobalErrorCode.INTERNAL_ERROR.getCode())
                        .message(GlobalErrorCode.INTERNAL_ERROR.getMessage())
                        .traceId(UUID.randomUUID().toString())
                        .build());
    }
}