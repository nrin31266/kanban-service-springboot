package com.rin.kanban.dto.response;


import com.rin.kanban.constant.PaymentMethod;
import com.rin.kanban.constant.Status;
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
public class OrderResponse {
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
    Instant updatedAt;
    String created;
    String updated;

    List<OrderProductResponse> orderProductResponses;
}
