package com.rin.kanban.controller;

import com.rin.kanban.dto.ApiResponse;
import com.rin.kanban.dto.PageResponse;
import com.rin.kanban.dto.request.FilterProductsRequest;
import com.rin.kanban.dto.request.ProductRequest;
import com.rin.kanban.dto.request.ProductsFilterValuesRequest;
import com.rin.kanban.dto.request.SoftDeleteRequest;
import com.rin.kanban.dto.response.ProductResponse;
import com.rin.kanban.pojo.ProductResult;
import com.rin.kanban.service.ProductService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.commons.security.ResourceServerTokenRelayAutoConfiguration;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

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
    public ApiResponse<PageResponse<ProductResponse>> getProducts(
            @RequestParam(required = false) String categoryIds,  // Danh sách categoryIds
            @RequestParam(required = false) String search,        // Từ khóa tìm kiếm
            @RequestParam(required = false, name = "rate") Integer rate,             // Đánh giá sản phẩm
            @RequestParam(required = false) BigDecimal max,  // Giá tối đa
            @RequestParam(required = false) BigDecimal min,  // Giá tối thiểu
            @RequestParam(required = false) String supplierIds, // Danh sách nhà cung cấp
            @RequestParam(defaultValue = "1") int page,           // Số trang
            @RequestParam(defaultValue = "9") int size            // Kích thước trang
    ) {

        FilterProductsRequest filterRequest = FilterProductsRequest.builder()
                .categoryIds(categoryIds)
                .search(search)
                .rate(rate)
                .maxPrice(max)
                .minPrice(min)
                .supplierIds(supplierIds)
                .build();

        log.info(filterRequest.toString());

        return ApiResponse.<PageResponse<ProductResponse>>builder()
                .result(productService.getProductsByFilterValues(filterRequest, page, size))
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

//    @PostMapping("/filter")
//    public ApiResponse<PageResponse<ProductResponse>> getProductsByFilter(@RequestBody @Validated ProductsFilterValuesRequest request) {
//        return ApiResponse.<PageResponse<ProductResponse>>builder()
//                .result(productService.getProductsByFilterValues(request))
//                .build();
//
//    }

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

    @GetMapping("/test/{productId}")
    public ApiResponse<ProductResult> test(@PathVariable("productId") String productId) {
        return ApiResponse.<ProductResult>builder()
                .result(productService.test(productId))
                .build();
    }
}
