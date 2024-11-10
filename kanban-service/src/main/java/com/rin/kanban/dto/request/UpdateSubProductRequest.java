package com.rin.kanban.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateSubProductRequest {
    Map<String, String> options;
    BigDecimal price;
    BigDecimal discount;
    BigDecimal quantity;
    String productId;
    Set<String> images;
}
