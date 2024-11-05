package com.rin.kanban.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.rin.kanban.entity.Category;
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
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductResponse {
    String id;
    String title;
    String description;
    String slug;
    String supplierId;
    String content;
    Instant expiredDate;
    Set<String> images;
    Set<String> categoryIds;
    Instant createdAt;
    Instant updatedAt;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    BigDecimal maxPrice;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    BigDecimal minPrice;
}
