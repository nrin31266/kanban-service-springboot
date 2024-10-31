package com.rin.kanban.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(9999, "An unexpected error occurred. Please try again later.", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_KEY(1001, "The provided key is invalid or missing. Please check your request and try again.", HttpStatus.BAD_REQUEST),
    INVALID_FIELD(10012, "One or more fields are invalid. Please review your input and correct any errors.", HttpStatus.BAD_REQUEST),
    UNAUTHORIZED(10013, "You are not authorized to access this resource.", HttpStatus.UNAUTHORIZED),
    UNAUTHENTICATED(10014, "You are not authenticated.", HttpStatus.UNAUTHORIZED),
    SUPPLIERS_EXISTS(10015, "Suppliers existed", HttpStatus.CONFLICT),
    SUPPLIERS_NOT_FOUND(10016, "Supplier not found", HttpStatus.NOT_FOUND),
    INVALID_EMAIL(10017, "Invalid email address", HttpStatus.BAD_REQUEST),
    CATEGORY_EXISTED(10018, "Category existed", HttpStatus.CONFLICT),
    CATEGORY_NOT_FOUND(10019, "Category not found", HttpStatus.NOT_FOUND),
    PRODUCT_NOT_FOUND(10020, "Product not found", HttpStatus.NOT_FOUND),
    EXISTED_PRODUCT(10021, "There are existing products with this category", HttpStatus.CONFLICT),
    SUB_PRODUCT_NOT_FOUND(10022, "Sub product not found", HttpStatus.NOT_FOUND),
    PROMOTION_NOT_FOUND(10023, "Promotion not found", HttpStatus.NOT_FOUND),
    PROMOTION_EXISTED(10024, "Promotion existed", HttpStatus.CONFLICT),
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
