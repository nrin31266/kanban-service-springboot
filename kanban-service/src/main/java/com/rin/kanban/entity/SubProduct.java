package com.rin.kanban.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

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
    BigDecimal price;
    Long quantity;
    Set<String> images;
    @DBRef
    Product product;
    @CreatedDate
    Instant createdAt;
    @LastModifiedDate
    Instant updatedAt;
}