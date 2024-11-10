package com.rin.kanban.dto.response;

import com.rin.kanban.constant.DiscountType;
import com.rin.kanban.entity.Product;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.DBRef;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SubProductResponse {
    String id;
    Map<String, String> options;
    BigDecimal price;
    BigDecimal quantity;
    Set<String> images;
    String productId;
    BigDecimal discount;
    Instant createdAt;
    Instant updatedAt;
}
