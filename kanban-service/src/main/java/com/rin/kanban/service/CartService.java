package com.rin.kanban.service;

import com.rin.kanban.dto.PageResponse;
import com.rin.kanban.dto.request.CartRequest;
import com.rin.kanban.dto.response.CartResponse;
import com.rin.kanban.dto.response.ProductResponse;
import com.rin.kanban.dto.response.SubProductResponse;
import com.rin.kanban.entity.Cart;
import com.rin.kanban.entity.SubProduct;
import com.rin.kanban.exception.AppException;
import com.rin.kanban.exception.ErrorCode;
import com.rin.kanban.mapper.CartMapper;
import com.rin.kanban.mapper.ProductMapper;
import com.rin.kanban.mapper.SubProductMapper;
import com.rin.kanban.repository.CartRepository;
import com.rin.kanban.repository.ProductRepository;
import com.rin.kanban.repository.SubProductRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CartService {
    CartMapper cartMapper;
    CartRepository cartRepository;
    SubProductRepository subProductRepository;
    SubProductMapper subProductMapper;
    ProductRepository productRepository;
    ProductMapper productMapper;

    public CartResponse addCart(CartRequest request) {
        if(cartRepository.findCart(request.getSubProductId(), request.getCreatedBy()).isPresent()){
            return updateCart(request);
        }
        Cart cart = cartMapper.toCart(request);
        return cartMapper.toCartResponse(cartRepository.save(cart));
    }

    public CartResponse updateCart(CartRequest request) {
        Cart cart = cartRepository.findCart(request.getSubProductId(), request.getCreatedBy()).orElseThrow(()-> new AppException(ErrorCode.CART_NOT_FOUND));
        SubProduct subProduct = subProductRepository.findById(request.getSubProductId()).orElseThrow(()-> new AppException(ErrorCode.SUB_PRODUCT_NOT_FOUND));


        cartMapper.updateCart(cart, request);

        int newCount = cart.getCount() + request.getCount();



        if(newCount > 100){
            newCount = 100;
        }

        if(newCount > subProduct.getQuantity()){
            newCount = subProduct.getQuantity();
        }

        cart.setCount(newCount);

        return cartMapper.toCartResponse(cartRepository.save(cart));
    }

    public void deleteCart(String subProductId, String createdBy) {
        Cart cart = cartRepository.findCart(subProductId, createdBy).orElseThrow(()-> new AppException(ErrorCode.CART_NOT_FOUND));
        cartRepository.delete(cart);
    }

    public PageResponse<CartResponse> getCarts(String createdBy, int page, int size) {
        Sort sort = Sort.by(Sort.Direction.DESC, "updatedAt");
        Pageable pageable = PageRequest.of(page - 1, size, sort);
        Page<Cart> pageData = cartRepository.findAllByCreatedByOrderByUpdatedAtDesc(createdBy, pageable);
        log.info(pageData.getContent().toString());
        List<CartResponse> cartsResponse = pageData.getContent().stream().map(cart -> {
            CartResponse cartResponse = cartMapper.toCartResponse(cart);
            cartResponse.setSubProductResponse(getSubProduct(cart.getSubProductId()));
            cartResponse.setProductResponse(getProductById(cart.getProductId()));
            return cartResponse;
        }).toList();

        return PageResponse.<CartResponse>builder()
                .data(cartsResponse)
                .totalPages(pageData.getTotalPages())
                .totalElements(pageData.getTotalElements())
                .pageSize(pageData.getNumberOfElements())
                .currentPage(page)
                .build();

    }

    private SubProductResponse getSubProduct(String subProductId) {
        return subProductRepository.findById(subProductId).map(subProductMapper::toSubProductResponse).orElse(null);
    }

    private ProductResponse getProductById(String productId) {
        return productRepository.findById(productId).map(productMapper::toProductResponse).orElse(null);
    }
}
