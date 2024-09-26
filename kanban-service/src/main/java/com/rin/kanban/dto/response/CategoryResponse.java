package com.rin.kanban.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.time.Instant;

@Data
@Document("category")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Category {
    @MongoId
    String id;
    String name;
    String description;
    String slug;
    @CreatedDate
    Instant createdAt;
    @LastModifiedDate
    Instant updatedAt;
}
