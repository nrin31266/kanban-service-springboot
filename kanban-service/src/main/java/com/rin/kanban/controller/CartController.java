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
    @GetMapping
    public ApiResponse<PageResponse<CartResponse>> getCarts(
            @RequestParam(name = "page", defaultValue = "1" , required = false) int page,
            @RequestParam(name = "size", defaultValue = "5", required = false) int size
    ) {
        return ApiResponse.<PageResponse<CartResponse>>builder()
                .result(cartService.getCarts(page, size))
                .build();
    }

    @GetMapping("/{subPrId}")
    public ApiResponse<CartResponse> getCart(
            @PathVariable("subPrId") String subPrId
    ) {
        return ApiResponse.<CartResponse>builder()
                .result(cartService.getCart(subPrId))
                .build();
    }

    @GetMapping("/additional")
    public ApiResponse<CartResponse> getCartAdditional(
            @RequestParam(name = "page", required = true) int page,
            @RequestParam(name = "size", required = true) int size
    ){
        return ApiResponse.<CartResponse>builder()
                .result(cartService.getAdditionalCart(page, size))
                .build();
    }

    @GetMapping("/to-payment")
    public ApiResponse<List<CartResponse>> getCartsToPayment(
            @RequestParam(name = "ids", required = true) String ids
            ){
        log.info(ids);
        return ApiResponse.<List<CartResponse>>builder()
                .result(cartService.getCartsToPayment(ids))
                .build();
    }

}
