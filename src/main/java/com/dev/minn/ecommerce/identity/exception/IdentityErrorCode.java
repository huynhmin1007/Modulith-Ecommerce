package com.dev.minn.ecommerce.identity.exception;

import com.dev.minn.ecommerce.common.exception.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum IdentityErrorCode implements BaseErrorCode {

    MISSING_TOKEN(1002, "Authentication token is missing from the request.", HttpStatus.UNAUTHORIZED),
    INVALID_TOKEN(1003, "The provided token is invalid or malformed.", HttpStatus.UNAUTHORIZED),
    TOKEN_EXPIRED(1004, "The authentication session has expired. Please log in again.", HttpStatus.UNAUTHORIZED),
    INVALID_AUTH_CODE(1005, "The authorization code is invalid or has already been used.", HttpStatus.UNAUTHORIZED),
    INVALID_REFRESH_TOKEN(1006, "The refresh token is invalid, expired, or has been revoked.", HttpStatus.UNAUTHORIZED),
    USER_NOT_FOUND(1007, "User not found.", HttpStatus.NOT_FOUND),
    INVALID_CREDENTIALS(1008, "Invalid credentials.", HttpStatus.UNAUTHORIZED),
    TOKEN_REVOKED(1009, "The authentication token has been revoked.", HttpStatus.UNAUTHORIZED),

    // 11xx: Authorization Errors (403 Forbidden)
    ACCESS_DENIED(1101, "You do not have permission to access this resource.", HttpStatus.FORBIDDEN),

    // 12xx: Client/Input Errors (400 Bad Request)
    LOGOUT_FAILED(1201, "Logout failed due to an invalid or missing token.", HttpStatus.BAD_REQUEST),
    USER_EXISTED(1202, "User already exists.", HttpStatus.BAD_REQUEST),
    OTP_EXPIRED(1203, "OTP has expired.", HttpStatus.BAD_REQUEST),
    INVALID_OTP(1204, "Invalid OTP.", HttpStatus.BAD_REQUEST),

    // 19xx: Identity Server/Integration Errors (502 / 500)
    IDENTITY_SERVER_ERROR(1999, "Identity provider is currently unavailable. Please try again later.", HttpStatus.BAD_GATEWAY);


    private final int code;
    private final String message;
    private final HttpStatus status;
}
