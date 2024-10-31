package com.rin.kanban.dto.response;


import com.rin.kanban.constant.DiscountType;
import lombok.*;
import lombok.experimental.FieldDefaults;


import java.math.BigDecimal;
import java.time.Instant;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults( level = AccessLevel.PRIVATE)
public class PromotionResponse {
    String id;
    String name;
    String description;
    String code;
    DiscountType discountType;
    BigDecimal value;
    Integer quantity;
    Instant start;
    Instant end;
    Instant created;
    Instant modified;
    String imageUrl;
}
