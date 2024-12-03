package com.rin.kanban.pojo;

import com.rin.kanban.dto.response.CategoryResponse;
import com.rin.kanban.dto.response.SupplierResponse;
import com.rin.kanban.entity.Category;
import com.rin.kanban.entity.SubProduct;
import com.rin.kanban.entity.Supplier;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Set;
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductResult {
    String id;
    String title;
    String description;
    String slug;
    String supplierId;
    String content;
    Instant expiredDate;
    Set<String> options;
    Set<String> images;
    Set<String> categoryIds;
    Instant createdAt;
    Instant updatedAt;
    //
    SupplierResponse supplierResponse;
    List<CategoryResponse> categoryResponse;
    BigDecimal maxPrice;
    BigDecimal minPrice;
    BigDecimal totalSold;
    //Rating
    BigDecimal countRating;
    float averageRating;
    //
    Supplier supplier;
    List<Category> categories;
    List<SubProduct> subProducts;

}
