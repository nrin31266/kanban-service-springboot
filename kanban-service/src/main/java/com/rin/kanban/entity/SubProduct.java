package com.rin.kanban.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@Document("sub-products")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SubProduct {
    String size;
    String color;
    BigDecimal price;
    Long quantity;
    @DBRef
    Product product;
    @CreatedDate
    Instant createdAt;
    @LastModifiedDate
    Instant updatedAt;
}
