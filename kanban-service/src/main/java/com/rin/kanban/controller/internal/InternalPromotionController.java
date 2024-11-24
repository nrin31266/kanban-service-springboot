package com.rin.kanban.controller.internal;

import com.rin.kanban.dto.ApiResponse;
import com.rin.kanban.dto.request.CheckDiscountCodeRequest;
import com.rin.kanban.dto.response.CheckDiscountCodeResponse;
import com.rin.kanban.service.PromotionService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/internal/promotions")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class InternalPromotionController {
    PromotionService promotionService;

    @PostMapping("/use-code")
    public ApiResponse<CheckDiscountCodeResponse> checkPromotionCode(
            @RequestBody CheckDiscountCodeRequest request
    ){
        return ApiResponse.<CheckDiscountCodeResponse>builder()
                .result(promotionService.useDiscountCode(request))
                .build();
    }
}
