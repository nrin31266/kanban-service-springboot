package com.rin.kanban.service;


import com.rin.kanban.constant.DiscountType;
import com.rin.kanban.constant.Status;
import com.rin.kanban.dto.PageResponse;
import com.rin.kanban.dto.request.DiscountCodeRequest;
import com.rin.kanban.dto.request.OrderProductRequest;
import com.rin.kanban.dto.request.OrderRequest;
import com.rin.kanban.dto.response.DiscountCodeResponse;
import com.rin.kanban.dto.response.OrderProductResponse;
import com.rin.kanban.dto.response.OrderResponse;
import com.rin.kanban.dto.response.ProductResponse;
import com.rin.kanban.entity.Order;
import com.rin.kanban.entity.OrderProduct;
import com.rin.kanban.entity.SubProduct;
import com.rin.kanban.exception.AppException;
import com.rin.kanban.exception.ErrorCode;
import com.rin.kanban.mapper.OrderMapper;
import com.rin.kanban.mapper.OrderProductMapper;
import com.rin.kanban.repository.OrderProductsRepository;
import com.rin.kanban.repository.OrderRepository;
import com.rin.kanban.repository.ProductRepository;
import com.rin.kanban.repository.SubProductRepository;
import com.rin.kanban.util.DateTimeFormatter;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Slf4j
public class OrderService {
    OrderRepository orderRepository;
    OrderMapper orderMapper;
    OrderProductsRepository orderProductsRepository;
    OrderProductMapper orderProductMapper;
    PromotionService promotionService;
    SubProductRepository subProductRepository;
    CartService cartService;
    DateTimeFormatter dateTimeFormatter;


    @Transactional(rollbackFor = AppException.class) // Đảm bảo rollback khi có lỗi
    public OrderResponse createOrder(OrderRequest orderRequest) {
        log.info(orderRequest.toString());


        Order order = orderMapper.toOrder(orderRequest);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();
        order.setUserId(userId);
        order.setStatus(Status.PENDING);
        order = orderRepository.save(order);
        String orderId = order.getId();
        List<OrderProductRequest> reduceProductQuantityResponse = reduceProducts(orderRequest.getOrderProductRequests());
        BigDecimal[] amount = {BigDecimal.ZERO};
        List<OrderProductResponse> orderProductResponses = new ArrayList<>();
        reduceProductQuantityResponse.forEach((item)->{
            item.setOrderId(orderId);
            OrderProduct orderProduct = orderProductsRepository.save(orderProductMapper.toOrderProducts(item));
            orderProductResponses.add(orderProductMapper.toOrderProductsResponse(orderProduct));
            cartService.deleteCart(item.getSubProductId(), userId);
            BigDecimal productTotal = orderProduct.getPrice().multiply(BigDecimal.valueOf(orderProduct.getCount()));
            amount[0] = amount[0].add(productTotal);
        });
        order.setAmount(amount[0]);
        if(orderRequest.getDiscountCode() != null && !orderRequest.getDiscountCode().isEmpty()) {
            DiscountCodeResponse discountCodeResponse = promotionService.useDiscountCode(DiscountCodeRequest.builder()
                    .discountCode(orderRequest.getDiscountCode())
                    .build());
            BigDecimal discountAmount = getReductionAmount(discountCodeResponse, order);
            order.setReduction(discountAmount);
            order.setAmount(order.getAmount().subtract(discountAmount));
        }
        order= orderRepository.save(order);
        OrderResponse orderResponse = orderMapper.toOrderResponse(order);
        orderResponse.setOrderProductResponses(orderProductResponses);

        return orderResponse;
    }


    private List<OrderProductRequest> reduceProducts(List<OrderProductRequest> request) {
        List<OrderProductRequest> reduceProducts = new ArrayList<>();
        List<String> subProductIds = new ArrayList<>();
        request.forEach(item -> subProductIds.add(item.getSubProductId()));

        // Lấy tất cả sản phẩm cần giảm số lượng một lần
        List<SubProduct> subProducts = subProductRepository.findAllById(subProductIds);

        request.forEach(item -> {
            SubProduct subProduct = subProducts.stream()
                    .filter(sp -> sp.getId().equals(item.getSubProductId()))
                    .findFirst()
                    .orElseThrow(() -> new AppException(ErrorCode.SUB_PRODUCT_NOT_FOUND));

            if (subProduct.getQuantity() - item.getCount() < 0) {
                throw new AppException(ErrorCode.PRODUCT_UN_STOCK);
            }

            subProduct.setQuantity(subProduct.getQuantity() - item.getCount());
            subProductRepository.save(subProduct);

            reduceProducts.add(OrderProductRequest.builder()
                    .count(item.getCount())
                    .price(subProduct.getDiscount() != null ? subProduct.getDiscount() : subProduct.getPrice())
                    .subProductId(item.getSubProductId())
                    .name(item.getName())
                    .imageUrl(item.getImageUrl())
                    .options(item.getOptions())
                    .productId(item.getProductId())
                    .build());
        });

        log.info(reduceProducts.toString());
        return reduceProducts;
    }


    private BigDecimal getReductionAmount(DiscountCodeResponse discountResponse, Order order) {
        BigDecimal discountAmount = BigDecimal.valueOf(0);
        if (discountResponse.getPromotionResponse().getDiscountType().equals(DiscountType.FIXED_AMOUNT)) {
            discountAmount = discountResponse.getPromotionResponse().getValue();
        } else if (discountResponse.getPromotionResponse().getDiscountType().equals(DiscountType.PERCENTAGE)) {
            BigDecimal discountPercentage = discountResponse.getPromotionResponse().getValue();
            discountAmount = order.getAmount().multiply(discountPercentage).divide(BigDecimal.valueOf(100));
        }
        return discountAmount;
    }

    public List<OrderResponse> getOrdersByUserIdAndStatus(String keyStatus) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Status status = Status.valueOf(keyStatus.toUpperCase());
        String userId = authentication.getName();
        Sort sort;
        if(status.equals(Status.PENDING)) {
            sort = Sort.by(Sort.Direction.DESC, "createdAt");
        }else{
            sort = Sort.by(Sort.Direction.DESC, "updatedAt");
        }
        List<Order> orders= orderRepository.findByUserIdAndStatus(userId, status, sort);
        return changeOrderResponse(orders);
    }

    private List<OrderResponse> changeOrderResponse(List<Order> orders) {
        List<OrderResponse> orderResponses = orders.stream().map(orderMapper::toOrderResponse).toList();
        orderResponses.stream().map(orderResponse -> {
            orderResponse.setCreated(dateTimeFormatter.formatDate(orderResponse.getCreatedAt()));
            orderResponse.setUpdated(dateTimeFormatter.formatDate(orderResponse.getUpdatedAt()));
            orderResponse.setOrderProductResponses(getOrderProductsByOrderId(orderResponse.getId()));
            return orderResponse;

        }).collect(Collectors.toList());
        return orderResponses;
    }

    private List<OrderProductResponse> getOrderProductsByOrderId(String orderId) {
        List<OrderProduct> orderProducts = orderProductsRepository.findByOrderId(orderId);
        return orderProducts.stream().map(orderProductMapper::toOrderProductsResponse).toList();
    };

    public PageResponse<OrderResponse> getOrderByStatus (String keyStatus, int page, int size) {
        Sort sort;
        Status status = Status.valueOf(keyStatus.toUpperCase());
        if(status.equals(Status.PENDING)) {
            sort = Sort.by(Sort.Direction.ASC, "createdAt");
        }else{
            sort = Sort.by(Sort.Direction.DESC, "updatedAt");
        }
        Pageable pageable = PageRequest.of(page - 1, size, sort);
        Page<Order> pageData = orderRepository.findByStatus(status,pageable);



        return PageResponse.<OrderResponse>builder()
                .pageSize(pageData.getSize())
                .currentPage(pageData.getNumber() + 1)
                .totalPages(pageData.getTotalPages())
                .totalElements(pageData.getTotalElements())
                .data(changeOrderResponse(pageData.getContent()))
                .build();
    }


    public void updateOrderStatus(String keyStatus, String orderId) {
        Status status = Status.valueOf(keyStatus.toUpperCase());
        Order order = orderRepository.findById(orderId).orElseThrow(()-> new AppException(ErrorCode.ORDER_NOT_FOUND));
        if(status.equals(Status.COMPLETED)) {
            order.setIsComplete(true);
        }
        order.setStatus(status);
        orderRepository.save(order);
    }


}
