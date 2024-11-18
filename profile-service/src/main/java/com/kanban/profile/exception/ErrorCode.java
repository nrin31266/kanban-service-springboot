package com.kanban.profile.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(9999, "An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_KEY(1001, "The provided key is invalid or missing", HttpStatus.BAD_REQUEST),
    INVALID_FIELD(10012, "One or more fields are invalid", HttpStatus.BAD_REQUEST),
    UNAUTHORIZED(10013, "You are not authorized to access this resource", HttpStatus.UNAUTHORIZED),
    UNAUTHENTICATED(10014, "You are not authenticated", HttpStatus.UNAUTHORIZED),
    ADDRESS_NOT_FOUND(5001, "Address not found", HttpStatus.NOT_FOUND),
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
