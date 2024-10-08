package com.rin.kanban.dto.request;

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
public class SubProductRequest {
    String size;
    String color;
    BigDecimal price;
    Long quantity;
    String productId;
    Set<String> images;
}
