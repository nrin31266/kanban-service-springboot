package com.rin.kanban.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.rin.kanban.entity.Category;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductHasSubProductsResponse {
    String id;
    String title;
    String description;
    String slug;
    String supplierId;
    String content;
    Instant expiredDate;
    Set<String> images;
    Set<Category> categories;
    Instant createdAt;
    Instant updatedAt;
    List<SubProductResponse> subProductResponse;
}
