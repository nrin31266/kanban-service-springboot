package com.rin.notification.constant;

import lombok.Getter;

@Getter
public enum PaymentMethod {
    CREDIT_CARD("CREDIT_CARD", "Credit Card"),
    PAYPAL("PAYPAL", "PayPal"),
    CASH_ON_DELIVERY("CASH_ON_DELIVERY", "Cash on Delivery");

    // Lấy key
    private final String key;
    // Lấy label
    private final String label;

    // Constructor
    PaymentMethod(String key, String label) {
        this.key = key;
        this.label = label;
    }

}
