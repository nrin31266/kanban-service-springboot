package com.kanban.payment.constant;

import lombok.Getter;

@Getter
public enum Status {
    PENDING("PENDING", "Pending"),
    CONFIRMED("CONFIRMED", "Confirmed"),
    SHIPPING("SHIPPING", "Shipping"),
    DELIVERED("DELIVERED", "Delivered"),
    COMPLETED("COMPLETED", "Completed");

    // Lấy key
    private final String key;
    // Lấy label
    private final String label;

    // Constructor
    Status(String key, String label) {
        this.key = key;
        this.label = label;
    }

}
