package com.rin.kanban.controller;

import com.rin.kanban.dto.ApiResponse;
import com.rin.kanban.dto.request.ProductRequest;
import com.rin.kanban.dto.response.ProductHasSubProductsResponse;
import com.rin.kanban.dto.response.ProductResponse;
import com.rin.kanban.service.ProductService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProductController {
    ProductService productService;

    @PostMapping
    public ApiResponse<ProductResponse> createProduct(@RequestBody ProductRequest productRequest) {
        return ApiResponse.<ProductResponse>builder()
                .result(productService.createProduct(productRequest))
                .build();
    }
    @GetMapping("/data")
    public ApiResponse<List<ProductHasSubProductsResponse>> getAllProducts() {
        return  ApiResponse.<List<ProductHasSubProductsResponse>>builder()
                .result(productService.getProducts())
                .build();
    }
    @GetMapping("/data/{productId}")
    public ApiResponse<ProductResponse> getProductById(@PathVariable("productId") String productId) {
        return ApiResponse.<ProductResponse>builder()
                .result(productService.getProduct(productId))
                .build();
    }
    @DeleteMapping("/{productId}")
    public ApiResponse deleteProductById(@PathVariable("productId") String productId) {
        Boolean isDeleted = productService.deleteProduct(productId);
        return ApiResponse.builder()
                .message(isDeleted? "Deleted" : "Can't be deleted")
                .build();
    }
    @PutMapping("/{productId}")
    public ApiResponse<ProductResponse> updateProduct(@PathVariable("productId") String productId ,@RequestBody ProductRequest productRequest) {
        return ApiResponse.<ProductResponse>builder()
                .result(productService.updateProduct(productId, productRequest))
                .build();
    }
}
