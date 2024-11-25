package com.rin.kanban.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;

@Document("order-product")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderProduct {
    @MongoId
    String id;
    String orderId;
    String name;
    String subProductId;
    String productId;
    @Field(targetType = FieldType.DECIMAL128)
    BigDecimal price;
    int count;
    Map<String, String> options;
    String imageUrl;
    @CreatedDate
    Instant createdAt;
    @LastModifiedDate
    Instant updatedAt;

}
