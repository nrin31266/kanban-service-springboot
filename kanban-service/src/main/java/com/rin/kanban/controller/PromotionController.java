package com.rin.kanban.controller;

import com.rin.kanban.dto.ApiResponse;
import com.rin.kanban.dto.request.CreatePromotionRequest;
import com.rin.kanban.dto.response.PromotionResponse;
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
@RequestMapping("/promotions")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PromotionController {
    PromotionService promotionService;
    @PostMapping
    public ApiResponse<PromotionResponse> createPromotion(@RequestBody CreatePromotionRequest createPromotionRequest) {
        return ApiResponse.<PromotionResponse>builder()
                .result(promotionService.createPromotion(createPromotionRequest))
                .build();
    }
}
