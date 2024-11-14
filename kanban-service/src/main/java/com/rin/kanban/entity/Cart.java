package com.rin.kanban.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;
import java.time.Instant;

@Data
@Document("carts")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Cart {
    @MongoId
    String id;
    String createdBy;
    String imageUrl;
    String subProductId;
    String productId;
    String title;
    int count;
    @CreatedDate
    Instant createdAt;
    @LastModifiedDate
    Instant updatedAt;
}
