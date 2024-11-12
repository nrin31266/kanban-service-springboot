package com.rin.kanban.service;

import com.rin.kanban.dto.request.CartRequest;
import com.rin.kanban.dto.response.CartResponse;
import com.rin.kanban.entity.Cart;
import com.rin.kanban.exception.AppException;
import com.rin.kanban.exception.ErrorCode;
import com.rin.kanban.mapper.CartMapper;
import com.rin.kanban.repository.CartRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CartService {
    CartMapper cartMapper;
    CartRepository cartRepository;
    public CartResponse addCart(CartRequest request) {
        Cart cart = cartMapper.toCart(request);
        return cartMapper.toCartResponse(cartRepository.save(cart));
    }

    public CartResponse updateCart(CartRequest request) {
        Cart cart = cartRepository.findCart(request.getSubProductId(), request.getCreatedBy()).orElseThrow(()-> new AppException(ErrorCode.CART_NOT_FOUND));
        cartMapper.updateCart(cart, request);
        return cartMapper.toCartResponse(cartRepository.save(cart));
    }

    public void deleteCart(String subProductId, String createdBy) {
        Cart cart = cartRepository.findCart(subProductId, createdBy).orElseThrow(()-> new AppException(ErrorCode.CART_NOT_FOUND));
        cartRepository.delete(cart);
    }
}
