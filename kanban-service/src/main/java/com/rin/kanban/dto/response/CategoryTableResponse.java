package com.rin.kanban.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CategoryTableResponse {
    String key;
    String name;
    String description;
    String slug;
    String parentId;
    Instant createdAt;
    Instant updatedAt;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    List<CategoryTableResponse> children;
}
