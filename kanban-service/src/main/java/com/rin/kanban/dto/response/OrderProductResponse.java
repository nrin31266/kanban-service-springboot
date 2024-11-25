package com.rin.kanban.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.util.Map;

@Document("products")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderProductResponse {
    String id;
    String orderId;
    String name;
    String subProductId;
    String productId;
    BigDecimal price;
    int count;
    Map<String, String> options;
    String imageUrl;
    String updatedAt;
    String createdAt;
}
