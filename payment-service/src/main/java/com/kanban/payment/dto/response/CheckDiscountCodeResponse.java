package com.kanban.payment.dto.response;

import com.rin.kanban.dto.response.PromotionResponse;
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
