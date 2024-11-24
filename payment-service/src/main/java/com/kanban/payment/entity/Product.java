package com.kanban.payment.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.math.BigDecimal;

@Document("products")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Product {
    @MongoId
    String id;
    String orderId;
    String name;
    String subProductId;
    String productId;
    @Field(targetType = FieldType.DECIMAL128)
    BigDecimal price;
    int count;
    String option;
    String imageUrl;
}
