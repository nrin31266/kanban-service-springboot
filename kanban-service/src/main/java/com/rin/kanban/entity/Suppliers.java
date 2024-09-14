package com.rin.kanban.entity;


import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Set;

@Data
@Document("suppliers")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Suppliers {
    @MongoId
    String id;
    String photoUrl;
    String name;
    String product;
    @DBRef
    Set<Category> categories;
    BigDecimal price;
    @Indexed(unique = true)
    String contact;
    boolean talking;
    @CreatedDate
    Instant createdAt;
    @LastModifiedDate
    Instant updatedAt;
}
