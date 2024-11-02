package com.rin.kanban.dto.request;

import com.rin.kanban.constant.DiscountType;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.CreatedDate;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdatePromotionRequest {
    String name;
    String description;
    String code;
    BigDecimal quantity;
    String discountType;
    BigDecimal value;
    Instant start;
    Instant end;
    @CreatedDate
    String imageUrl;
}
