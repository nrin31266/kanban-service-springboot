package com.rin.kanban.controller;

import com.rin.kanban.dto.ApiResponse;
import com.rin.kanban.dto.PageResponse;
import com.rin.kanban.dto.request.CheckDiscountCodeRequest;
import com.rin.kanban.dto.request.CreatePromotionRequest;
import com.rin.kanban.dto.request.SoftDeleteRequest;
import com.rin.kanban.dto.request.UpdatePromotionRequest;
import com.rin.kanban.dto.response.CheckDiscountCodeResponse;
import com.rin.kanban.dto.response.PromotionResponse;
import com.rin.kanban.service.PromotionService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "size", defaultValue = "10") Integer size
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
    @PutMapping("/soft-delete/{promotionId}")
    public ApiResponse<PromotionResponse> softDeletePromotion(
            @PathVariable("promotionId") String promotionId
    ){
        return ApiResponse.<PromotionResponse>builder()
                .result(promotionService.softDeletePromotion(promotionId))
                .build();
    }
    @PutMapping("/soft-delete")
    public ApiResponse<List<PromotionResponse>> softDeletePromotions(
            @RequestBody SoftDeleteRequest request
            ){
        return ApiResponse.<List<PromotionResponse>>builder()
                .result(promotionService.softDeletePromotions(request))
                .build();
    }
    @PostMapping("/check-code")
    public ApiResponse<CheckDiscountCodeResponse> checkPromotionCode(
            @RequestBody CheckDiscountCodeRequest request
            ){
        return ApiResponse.<CheckDiscountCodeResponse>builder()
                .result(promotionService.checkDiscountCode(request))
                .build();
    }
}
