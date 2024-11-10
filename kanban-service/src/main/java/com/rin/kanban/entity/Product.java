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
import java.util.List;
import java.util.Map;
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
    String content = null;
    Instant expiredDate = null;
    String[] images;
    boolean isDeleted = false;
    Set<String> categoryIds;
    Set<String> options;
    @CreatedDate
    Instant createdAt;
    @LastModifiedDate
    Instant updatedAt;

//    // This field will contain result from lookup
//    List<SubProduct> subProducts;
}
