package com.rin.kanban.controller;



import com.rin.kanban.constant.Status;
import com.rin.kanban.dto.ApiResponse;
import com.rin.kanban.dto.PageResponse;
import com.rin.kanban.dto.request.OrderRequest;
import com.rin.kanban.dto.response.OrderResponse;
import com.rin.kanban.service.OrderService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderController {
    OrderService orderService;
    @PostMapping
    public ApiResponse<OrderResponse> createOrder (@RequestBody OrderRequest orderRequest) {
        return ApiResponse.<OrderResponse>builder()
                .result(orderService.createOrder(orderRequest))
                .build();
    }

    @GetMapping
    public ApiResponse<List<OrderResponse>> getCustomerOrders(
            @RequestParam("status") String status
            ) {
            return ApiResponse.<List<OrderResponse>>builder()
                    .result(orderService.getOrdersByUserIdAndStatus(status))
                    .build();
    }

    @GetMapping("/ad")
    public ApiResponse<PageResponse<OrderResponse>> getOrders(
            @RequestParam("page") int page,
            @RequestParam("size") int size,
            @RequestParam(value = "status", required = false) String status
    ){
        return ApiResponse.<PageResponse<OrderResponse>>builder()
                .result(orderService.getOrderByStatus(status, page, size))
                .build();
    }
}
