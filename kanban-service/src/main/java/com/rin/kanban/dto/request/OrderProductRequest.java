package com.rin.kanban.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderProductRequest {
    String orderId;
    String name;
    String subProductId;
    String productId;
    BigDecimal price;
    int count;
    Map<String, String> options;
    String imageUrl;
}
