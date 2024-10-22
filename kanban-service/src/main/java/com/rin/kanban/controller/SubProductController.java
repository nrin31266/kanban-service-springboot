package com.rin.kanban.controller;

import com.rin.kanban.dto.ApiResponse;
import com.rin.kanban.dto.request.SubProductRequest;
import com.rin.kanban.dto.response.SelectDataResponse;
import com.rin.kanban.dto.response.SubProductResponse;
import com.rin.kanban.service.SubProductService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
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
}
