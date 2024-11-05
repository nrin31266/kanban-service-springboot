package com.rin.kanban.controller;

import com.rin.kanban.dto.ApiResponse;
import com.rin.kanban.dto.PageResponse;
import com.rin.kanban.dto.request.SoftDeleteRequest;
import com.rin.kanban.dto.request.SubProductRequest;
import com.rin.kanban.dto.request.UpdateSubProductRequest;
import com.rin.kanban.dto.response.SelectDataResponse;
import com.rin.kanban.dto.response.SubProductResponse;
import com.rin.kanban.service.SubProductService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/sub-products")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SubProductController {
    SubProductService subProductService;
    @PostMapping
    public ApiResponse<SubProductResponse> createSubProduct(@RequestBody SubProductRequest subProductRequest) {
        log.info(subProductRequest.toString());
        return ApiResponse.<SubProductResponse>builder()
                .result(subProductService.createSubProduct(subProductRequest))
                .build();
    }
    @GetMapping("/filter-values")
    public ApiResponse<List<SelectDataResponse>> getSubProducts() {
        return ApiResponse.<List<SelectDataResponse>>builder()
                .result(subProductService.getFilterValues())
                .build();
    }
    @GetMapping("/product-detail/{productId}")
    public ApiResponse<List<SubProductResponse>> getSubProductsByProductId(@PathVariable("productId") String productId) {
        return ApiResponse.<List<SubProductResponse>>builder()
                .result(subProductService.getSubProductsByProductId(productId))
                .build();
    }
    @GetMapping
    public ApiResponse<PageResponse<SubProductResponse>> getSubProducts(@RequestParam(name = "page", defaultValue = "1") int page, @RequestParam(name = "size", defaultValue = "10") int size) {
        return ApiResponse.<PageResponse<SubProductResponse>>builder()
                .result(subProductService.getSubProducts(page, size))
                .build();
    }

    @PutMapping("/soft-delete")
    public ApiResponse<List<SubProductResponse>> softDeleteSubProduct(@RequestBody SoftDeleteRequest subProductRequest) {
        return ApiResponse.<List<SubProductResponse>>builder()
                .result(subProductService.softDeleteProduct(subProductRequest))
                .build();
    }
    @PutMapping("/{subProductId}")
    public ApiResponse<SubProductResponse> updateSubProduct(@RequestBody UpdateSubProductRequest updateSubProductRequest, @PathVariable("subProductId") String subProductId) {
        return ApiResponse.<SubProductResponse>builder()
                .result(subProductService.updateSubProduct(updateSubProductRequest, subProductId))
                .build();
    }
}
