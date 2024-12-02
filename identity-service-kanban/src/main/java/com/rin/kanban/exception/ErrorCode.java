package com.rin.kanban.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(9999, "Unexpected error. Try again later", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_KEY(1001, "Invalid key", HttpStatus.BAD_REQUEST),
    UNAUTHENTICATED(1006, "Unauthenticated", HttpStatus.UNAUTHORIZED),
    INCORRECT_LOGIN_INFORMATION(10014, "Incorrect login info", HttpStatus.BAD_REQUEST),
    UNAUTHORIZED(1007, "Access denied", HttpStatus.FORBIDDEN),
    PASSWORD_INVALID(1008, "Age must be at least {min}", HttpStatus.BAD_REQUEST),
    EMAIL_EXISTS(1009, "Email already in use", HttpStatus.BAD_REQUEST),
    USER_NOT_FOUND(10010, "User not found", HttpStatus.NOT_FOUND),
    INVALID_EMAIL(10011, "Invalid email address", HttpStatus.BAD_REQUEST),
    INVALID_FIELD(10012, "Invalid field(s). Check input", HttpStatus.BAD_REQUEST),
    TOKEN_EXPIRED(10013, "Token expired. Get a new one", HttpStatus.UNAUTHORIZED),
    ROLE_NOT_FOUND(10014, "Role not found", HttpStatus.NOT_FOUND),
    OTP_ERROR(10015, "OTP error", HttpStatus.BAD_REQUEST),
    OTP_EXPIRED(10016, "OTP expired, please request a new one", HttpStatus.BAD_REQUEST),
    OTP_USED(10017, "OTP already used, please request a new one", HttpStatus.BAD_REQUEST),
    OTP_REFRESH(10018, "Please refresh OTP", HttpStatus.BAD_REQUEST),
    OTP_INCORRECT(10019, "OTP is incorrect, please try again", HttpStatus.BAD_REQUEST),
    ;

    ErrorCode(int code, String message, HttpStatusCode statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }

    private final int code;
    private final String message;
    private final HttpStatusCode statusCode;
}
