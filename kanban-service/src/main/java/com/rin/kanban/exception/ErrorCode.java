package com.rin.kanban.exception;

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
    DISCOUNT_INVALID(10025, "Discount is invalid", HttpStatus.BAD_REQUEST),
    CART_NOT_FOUND(10026, "Cart not found", HttpStatus.NOT_FOUND),
    PROMOTION_UN_STOCK(10027, "Promotion un stock", HttpStatus.CONFLICT),
    DISCOUNT_NOT_YET_AVAILABLE(10028, "Discount not yet available", HttpStatus.CONFLICT),
    DISCOUNT_ENDED(10029, "Discount ended", HttpStatus.CONFLICT),
    PRODUCT_UN_STOCK(10030, "Product un stock", HttpStatus.CONFLICT),
    ORDER_NOT_FOUND(10031, "Order not found", HttpStatus.NOT_FOUND),
    CAN_NOT_RATING(10032, "Can not rating", HttpStatus.CONFLICT),
    RATING_NOT_FOUND(10033, "Rating not found", HttpStatus.NOT_FOUND),
    RATING_EXISTED(10034, "Rating existed", HttpStatus.CONFLICT),
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
