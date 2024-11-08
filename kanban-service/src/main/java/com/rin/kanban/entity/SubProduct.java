package com.rin.kanban.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Set;

@Data
@Document("sub-products")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SubProduct {
    @MongoId
    String id;
    String size;
    String color;
    @Field(targetType = FieldType.DECIMAL128)
    BigDecimal price;
    @Field(targetType = FieldType.DECIMAL128)
    BigDecimal quantity;
    @Field(targetType = FieldType.DECIMAL128)
    BigDecimal discount;
    Set<String> images;
    String productId;
    boolean isDeleted = false;
    @CreatedDate
    Instant createdAt;
    @LastModifiedDate
    Instant updatedAt;

}
