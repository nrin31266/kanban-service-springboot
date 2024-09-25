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
@Document("products")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Product {
    @MongoId
    String id;
    String title;
    String description;
    String slug;
    String supplierId;
    String[] imageUrl;
    @DBRef
    Set<Category> categories;
    @CreatedDate
    Instant createdAt;
    @LastModifiedDate
    Instant updatedAt;

}
