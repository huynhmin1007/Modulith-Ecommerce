package com.dev.minn.ecommerce.config.web;

import com.dev.minn.ecommerce.common.application.dto.ApiResponse;
import com.dev.minn.ecommerce.common.application.exception.BaseErrorCode;
import com.dev.minn.ecommerce.common.application.exception.BusinessException;
import com.dev.minn.ecommerce.common.application.exception.GlobalErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice(basePackages = "com.dev.minn")
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleAppException(BusinessException e) {
        BaseErrorCode code = e.getErrorCode();
        log.warn("AppException: {} - {}", code.getCode(), code.getMessage());

        return ResponseEntity
                .status(code.getStatus())
                .body(ApiResponse.error(code.getCode(), code.getMessage(), null, null));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidation(MethodArgumentNotValidException e) {
        Map<String, Object> errors = new HashMap<>();

        for (FieldError error : e.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }

        log.warn("Validation Error: {}", errors);

        return ResponseEntity
                .badRequest()
                .body(ApiResponse.error(
                        GlobalErrorCode.INVALID_REQUEST.getCode(),
                        GlobalErrorCode.INVALID_REQUEST.getMessage(),
                        null,
                        errors)
                );
    }

    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<ApiResponse<Void>> handleAccessDenied(AuthorizationDeniedException e) {
        log.warn("Access Denied: {}", e.getMessage());

        return ResponseEntity
                .status(403)
                .body(ApiResponse.error(
                        GlobalErrorCode.UNAUTHORIZED.getCode(),
                        GlobalErrorCode.UNAUTHORIZED.getMessage(),
                        null,
                        null
                ));
    }

    // 3. Bắt các lỗi vỡ hệ thống (NullPointer, đứt mạng...)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleUnexpected(Exception e) {
        log.error("Unhandled Exception: ", e);

        return ResponseEntity
                .internalServerError()
                .body(ApiResponse.error(
                        GlobalErrorCode.INTERNAL_ERROR.getCode(),
                        GlobalErrorCode.INTERNAL_ERROR.getMessage(),
                        null,
                        null
                ));
    }
}