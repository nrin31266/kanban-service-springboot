package com.rin.kanban.controller;

import com.rin.kanban.dto.ApiResponse;
import com.rin.kanban.dto.PageResponse;
import com.rin.kanban.dto.request.ProductRequest;
import com.rin.kanban.dto.request.ProductsFilterValuesRequest;
import com.rin.kanban.dto.request.SoftDeleteRequest;
import com.rin.kanban.dto.response.ProductResponse;
import com.rin.kanban.service.ProductService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.commons.security.ResourceServerTokenRelayAutoConfiguration;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProductController {
    ProductService productService;
    private final ResourceServerTokenRelayAutoConfiguration resourceServerTokenRelayAutoConfiguration;

    @PostMapping
    public ApiResponse<ProductResponse> createProduct(@RequestBody ProductRequest productRequest) {
        return ApiResponse.<ProductResponse>builder()
                .result(productService.createProduct(productRequest))
                .build();
    }

    @GetMapping
    public ApiResponse<PageResponse<ProductResponse>> getProducts() {
        List<ProductResponse> products = productService.getProducts();
        return ApiResponse.<PageResponse<ProductResponse>>builder()
                .result(PageResponse.<ProductResponse>builder()
                        .data(products)
                        .pageSize(1)
                        .currentPage(1)
                        .totalPages(1)
                        .totalElements(products.size())
                        .build())
                .build();
    }

    @GetMapping("/data")
    public ApiResponse<PageResponse<ProductResponse>> getAllProducts(
            @RequestParam(required = false, value = "title") String title,
            @RequestParam(required = false, value = "page") Integer page,
            @RequestParam(required = false, value = "size") Integer size) {
        if (title != null && page != null && size != null) {
            return ApiResponse.<PageResponse<ProductResponse>>builder()
                    .result(productService.getProductsPaginationAndTitle(page, size, title))
                    .build();
        } else if (page != null && size != null) {
            return ApiResponse.<PageResponse<ProductResponse>>builder()
                    .result(productService.getProductsPagination(page, size))
                    .build();
        }
        return null;
    }

    @GetMapping("/{productId}")
    public ApiResponse<ProductResponse> getProductById(@PathVariable("productId") String productId) {
        return ApiResponse.<ProductResponse>builder()
                .result(productService.getProduct(productId))
                .build();
    }

    @DeleteMapping("/{productId}")
    public ApiResponse deleteProductById(@PathVariable("productId") String productId) {
        Boolean isDeleted = productService.deleteProduct(productId);
        return ApiResponse.builder()
                .result(isDeleted)
                .message(isDeleted ? "Deleted" : "Can't be deleted")
                .build();
    }

    @PutMapping("/{productId}")
    public ApiResponse<ProductResponse> updateProduct(@PathVariable("productId") String productId, @RequestBody ProductRequest productRequest) {
        return ApiResponse.<ProductResponse>builder()
                .result(productService.updateProduct(productId, productRequest))
                .build();
    }

    @PutMapping("/soft-delete")
    public ApiResponse<Void> softDeleteProduct(@RequestBody SoftDeleteRequest softDeleteRequest) {
        productService.softDeleteProduct(softDeleteRequest);
        return ApiResponse.<Void>builder()
                .message("Deleted")
                .build();
    }

    @PostMapping("/filter")
    public ApiResponse<PageResponse<ProductResponse>> getProductsByFilter(@RequestBody @Validated ProductsFilterValuesRequest request) {
        return ApiResponse.<PageResponse<ProductResponse>>builder()
                .result(productService.getProductsByFilterValues(request))
                .build();

    }

    @GetMapping("/bestseller")
    public ApiResponse<List<ProductResponse>> getBestSellerProducts() {
        return ApiResponse.<List<ProductResponse>>builder()
                .result(productService.getBestsellerProducts())
                .build();
    }

    @GetMapping("/related/{productId}")
    public ApiResponse<List<ProductResponse>> getRelatedProducts(@PathVariable("productId") String productId) {
        return ApiResponse.<List<ProductResponse>>builder()
                .result(productService.getRelatedProducts(productId))
                .build();
    }
}
