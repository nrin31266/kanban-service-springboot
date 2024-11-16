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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

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
        if (cartRepository.findCart(request.getSubProductId(), request.getCreatedBy()).isPresent()) {
            return updateCartCount(request);
        }
        Cart cart = cartMapper.toCart(request);
        CartResponse cartResponse = cartMapper.toCartResponse(cartRepository.save(cart));
        cartResponse.setProductResponse(getProductById(cart.getProductId()));
        cartResponse.setSubProductResponse(getSubProduct(cart.getSubProductId()));
        cartResponse.setIsCreated(true);
        return cartResponse;
    }

    public CartResponse updateCart(CartRequest request) {
        Cart cart = cartRepository.findCart(request.getSubProductId(), request.getCreatedBy()).orElseThrow(() -> new AppException(ErrorCode.CART_NOT_FOUND));
        SubProduct subProduct = subProductRepository.findById(request.getSubProductId()).orElseThrow(() -> new AppException(ErrorCode.SUB_PRODUCT_NOT_FOUND));


        cartMapper.updateCart(cart, request);

        int newCount = request.getCount();


        if (newCount > 100) {
            newCount = 100;
        }
        if (newCount > subProduct.getQuantity()) {
            newCount = subProduct.getQuantity();
        }

        cart.setCount(newCount);
        CartResponse cartResponse = cartMapper.toCartResponse(cartRepository.save(cart));
        cartResponse.setProductResponse(getProductById(cart.getProductId()));
        cartResponse.setSubProductResponse(getSubProduct(cart.getSubProductId()));

        return cartResponse;
    }

    private CartResponse updateCartCount(CartRequest request) {
        Cart cart = cartRepository.findCart(request.getSubProductId(), request.getCreatedBy()).orElseThrow(() -> new AppException(ErrorCode.CART_NOT_FOUND));
        SubProduct subProduct = subProductRepository.findById(request.getSubProductId()).orElseThrow(() -> new AppException(ErrorCode.SUB_PRODUCT_NOT_FOUND));
        cartMapper.updateCart(cart, request);
        int newCount = cart.getCount() + request.getCount();
        if (newCount > 100) {
            newCount = 100;
        }
        if (newCount > subProduct.getQuantity()) {
            newCount = subProduct.getQuantity();
        }
        cart.setCount(newCount);
        CartResponse cartResponse = cartMapper.toCartResponse(cartRepository.save(cart));
        cartResponse.setProductResponse(getProductById(cart.getProductId()));
        cartResponse.setSubProductResponse(getSubProduct(cart.getSubProductId()));

        return cartResponse;
    }

    public void deleteCart(String subProductId, String createdBy) {
        Cart cart = cartRepository.findCart(subProductId, createdBy).orElseThrow(() -> new AppException(ErrorCode.CART_NOT_FOUND));
        cartRepository.delete(cart);
    }


    public PageResponse<CartResponse> getCarts(int page, int size) {


        Sort sort = Sort.by(Sort.Direction.DESC, "updatedAt");
        Pageable pageable = PageRequest.of(page - 1, size, sort);
        Page<Cart> pageData = cartRepository.findAllByCreatedByOrderByUpdatedAtDesc(getUserId(), pageable);
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

    public CartResponse getAdditionalCart(int page, int size) {

        Sort sort = Sort.by(Sort.Direction.DESC, "updatedAt");
        //next page
        Pageable pageable = PageRequest.of(page - 1, size, sort);
        Page<Cart> pageData = cartRepository.findAllByCreatedByOrderByUpdatedAtDesc(getUserId(), pageable);

        CartResponse cartResponse = cartMapper.toCartResponse(pageData.getContent().getLast());
        cartResponse.setProductResponse(getProductById(cartResponse.getProductId()));
        cartResponse.setSubProductResponse(getSubProduct(cartResponse.getSubProductId()));
        return cartResponse;
    }

    public List<CartResponse> getCartsToPayment(String request) {
        String[] idList = request.split(",");
        log.info(Arrays.toString(idList));
        return Arrays.stream(idList).map(this::getCart).toList();
    }

    private SubProductResponse getSubProduct(String subProductId) {
        return subProductRepository.findById(subProductId).map(subProductMapper::toSubProductResponse).orElse(null);
    }

    private ProductResponse getProductById(String productId) {
        return productRepository.findById(productId).map(productMapper::toProductResponse).orElse(null);
    }


    public CartResponse getCart(String subPrId) {

        CartResponse cartResponse = cartMapper.toCartResponse(cartRepository.findCart(subPrId, getUserId()).orElseThrow(() -> new AppException(ErrorCode.CART_NOT_FOUND)));
        cartResponse.setProductResponse(getProductById(cartResponse.getProductId()));
        cartResponse.setSubProductResponse(getSubProduct(cartResponse.getSubProductId()));
        return cartResponse;
    }

    private String getUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }
}
