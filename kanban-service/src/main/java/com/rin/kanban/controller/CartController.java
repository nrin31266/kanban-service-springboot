package com.rin.kanban.controller;

import com.rin.kanban.dto.ApiResponse;
import com.rin.kanban.dto.PageResponse;
import com.rin.kanban.dto.request.CartRequest;
import com.rin.kanban.dto.response.CartResponse;
import com.rin.kanban.service.CartService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/carts")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CartController {
    CartService cartService;
    @PostMapping
    public ApiResponse<CartResponse> addCart(@RequestBody CartRequest request ) {
        log.info(request.toString());
        return ApiResponse.<CartResponse>builder()
                .result(cartService.addCart(request))
                .build();
    }

    @PutMapping
    public ApiResponse<CartResponse> updateCart(@RequestBody CartRequest request ) {
        log.info(request.toString());
        return ApiResponse.<CartResponse>builder()
                .result(cartService.updateCart(request))
                .build();
    }

    @DeleteMapping("/{subProductId}/{createdBy}")
    public ApiResponse<Object> deleteCart(@PathVariable(name = "subProductId") String subProductId, @PathVariable(name = "createdBy") String createdBy) {
        cartService.deleteCart(subProductId, createdBy);
        return ApiResponse.builder()
                .result("Deleted successfully")
                .build();
    }
    @GetMapping("/{createdBy}")
    public ApiResponse<PageResponse<CartResponse>> getCarts(
            @PathVariable(name = "createdBy") String createdBy,
            @RequestParam(name = "page", defaultValue = "1" , required = false) int page,
            @RequestParam(name = "size", defaultValue = "5", required = false) int size
    ) {
        return ApiResponse.<PageResponse<CartResponse>>builder()
                .result(cartService.getCarts(createdBy, page, size))
                .build();
    }

}
