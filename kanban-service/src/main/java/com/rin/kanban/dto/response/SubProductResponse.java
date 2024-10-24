package com.rin.kanban.dto.response;

import com.rin.kanban.entity.Product;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.DBRef;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SubProductResponse {
    String id;
    String size;
    String color;
    BigDecimal price;
    BigDecimal quantity;
    Set<String> images;
    String productId;
    Instant createdAt;
    Instant updatedAt;
}
