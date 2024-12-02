package com.rin.notification.dto.noname;


import com.rin.notification.constant.PaymentMethod;
import com.rin.notification.constant.Status;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Order {
    String id;
    String userId;
    String customerName;
    String customerPhone;
    String customerEmail;
    String customerAddress;
    PaymentMethod paymentMethod;
    String discountCode;
    Status status;
    BigDecimal reduction;
    BigDecimal amount;
    Instant createdAt;
    Boolean isComplete;
    Instant updatedAt;
    String created;
    String updated;

    List<OrderProduct> orderProductResponses;
}
