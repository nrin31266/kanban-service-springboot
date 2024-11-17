package com.kanban.payment.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(9999, "An unexpected error occurred. Please try again later.", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_KEY(1001, "The provided key is invalid or missing. Please check your request and try again.", HttpStatus.BAD_REQUEST),
    UNAUTHENTICATED(1006, "Authentication required. Please log in to access this resource.", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(1007, "You do not have the necessary permissions to access this resource.", HttpStatus.FORBIDDEN),
    PASSWORD_INVALID(1008, "Password is invalid. It must be at least 8 characters long and meet the required criteria.", HttpStatus.BAD_REQUEST),
    EMAIL_EXISTS(1009, "The email address is already in use. Please use a different email address.", HttpStatus.BAD_REQUEST),
    USER_NOT_FOUND(10010, "User not found. Please check the user ID or details and try again.", HttpStatus.NOT_FOUND),
    INVALID_EMAIL(10011, "The email address provided is not valid. Please enter a valid email address.", HttpStatus.BAD_REQUEST),
    INVALID_FIELD(10012, "One or more fields are invalid. Please review your input and correct any errors.", HttpStatus.BAD_REQUEST),
    TOKEN_EXPIRED(10013, "The token has expired. Please obtain a new token and try again.", HttpStatus.UNAUTHORIZED);;

    ErrorCode(int code, String message, HttpStatusCode statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }

    private final int code;
    private final String message;
    private final HttpStatusCode statusCode;
}