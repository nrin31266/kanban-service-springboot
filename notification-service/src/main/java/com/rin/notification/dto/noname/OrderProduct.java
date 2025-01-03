package com.rin.notification.dto.noname;

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
public class OrderProduct {
    String id;
    String orderId;
    String name;
    String subProductId;
    String productId;
    BigDecimal price;
    int count;
    Map<String, String> options;
    Boolean isRating;
    String imageUrl;
    String updatedAt;
    String createdAt;
}
