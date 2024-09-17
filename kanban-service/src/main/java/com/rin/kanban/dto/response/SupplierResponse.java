package com.rin.kanban.dto.response;

import com.rin.kanban.entity.Category;
import jakarta.validation.constraints.Email;
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
public class SupplierResponse {
    String id;
    int onTheWay;
    String email;
    String photoUrl;
    String slug;
    String name;
    String product;
    Set<Category> categories;
    BigDecimal price;
    String contact;
    boolean talking;
    Instant createdAt;
    Instant updatedAt;
}
