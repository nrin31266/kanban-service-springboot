package com.rin.kanban.dto.request;

import com.rin.kanban.entity.Category;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.time.Instant;
import java.util.Map;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductRequest {
    String title;
    String description;
    String slug;
    Integer quantity;
    String supplierId;
    String content;
    Instant expiredDate;
    Set<String> options;
    Set<String> images;
    Set<String> categoryIds;
}
