package com.rin.kanban.controller;

import com.rin.kanban.dto.ApiResponse;
import com.rin.kanban.dto.PageResponse;
import com.rin.kanban.dto.request.CreatePromotionRequest;
import com.rin.kanban.dto.request.UpdatePromotionRequest;
import com.rin.kanban.dto.response.PromotionResponse;
import com.rin.kanban.service.PromotionService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/promotions")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PromotionController {
    PromotionService promotionService;
    @PostMapping
    public ApiResponse<PromotionResponse> createPromotion(@RequestBody CreatePromotionRequest createPromotionRequest) {
        log.info(createPromotionRequest.toString());
        return ApiResponse.<PromotionResponse>builder()
                .result(promotionService.createPromotion(createPromotionRequest))
                .build();
    }
    @GetMapping
    public ApiResponse<PageResponse<PromotionResponse>> getPromotions(
            @RequestParam("page") Integer page,
            @RequestParam("size") Integer size
    ) {
        return ApiResponse.<PageResponse<PromotionResponse>>builder()
                .result(promotionService.getPromotions(page, size))
                .build();
    }
    @PutMapping("/{promotionId}")
    public ApiResponse<PromotionResponse> updatePromotion(
            @PathVariable("promotionId") String promotionId,
            @RequestBody UpdatePromotionRequest updatePromotionRequest
    ){
        return ApiResponse.<PromotionResponse>builder()
                .result(promotionService.updatePromotion(updatePromotionRequest, promotionId))
                .build();
    }
    @PutMapping("/{promotionId}")
    public ApiResponse<PromotionResponse> softDeletePromotion(
            @PathVariable("promotionId") String promotionId
    ){
        return ApiResponse.<PromotionResponse>builder()
                .result(promotionService.softDeletePromotion(promotionId))
                .build();
    }
}
