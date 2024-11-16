package com.rin.kanban.dto.response;

import com.rin.kanban.entity.Promotion;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CheckDiscountCodeResponse {
    Boolean isValid;
    PromotionResponse promotionResponse;
    String message;
}
