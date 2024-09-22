package com.rin.kanban.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.rin.kanban.entity.Category;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ExportSupplierDataResponse {
    String id;
    Integer onTheWay;
    String email;
    String photoUrl;
    String slug;
    String name;
    String product;
    Set<Category> categories;
    BigDecimal price;
    String contact;
    Boolean talking;
    Instant createdAt;
    Instant updatedAt;
}
