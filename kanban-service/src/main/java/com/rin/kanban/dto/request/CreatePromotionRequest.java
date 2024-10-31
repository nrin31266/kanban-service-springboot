package com.rin.kanban.dto.request;

import com.rin.kanban.constant.DiscountType;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreatePromotionRequest {
    String name;
    String description;
    String code;
    //Enum
    Boolean discountType;
    BigDecimal value;
    Instant start;
    Instant end;
    @CreatedDate
    String imageUrl;
}
