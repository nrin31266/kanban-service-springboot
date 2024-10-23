package com.rin.kanban.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductsFilterValuesRequest {
    Set<String> categoriesId;
    Set<String> colors;
    Set<String> sizes;
    BigDecimal minPrice;
    BigDecimal maxPrice;
}
