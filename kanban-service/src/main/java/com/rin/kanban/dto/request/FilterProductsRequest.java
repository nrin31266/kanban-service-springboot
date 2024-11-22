package com.rin.kanban.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FilterProductsRequest {
    String categoryIds;
    String search;
    Integer rate;
    BigDecimal maxPrice;
    BigDecimal minPrice;
    String supplierIds;
}
