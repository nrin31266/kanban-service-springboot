package com.rin.kanban.dto.response;

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
public class SuppliersResponse {
    String id;
    String photoUrl;
    String name;
    String product;
    Set<Category> categories;
    BigDecimal price;
    String contact;
    boolean talking;
    Instant createdAt;
    Instant updatedAt;
}
