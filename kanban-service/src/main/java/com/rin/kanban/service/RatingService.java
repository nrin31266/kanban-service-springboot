package com.rin.kanban.service;

import com.rin.kanban.dto.request.RatingRequest;
import com.rin.kanban.dto.response.RatingResponse;
import com.rin.kanban.entity.Order;
import com.rin.kanban.entity.OrderProduct;
import com.rin.kanban.entity.Rating;
import com.rin.kanban.exception.AppException;
import com.rin.kanban.exception.ErrorCode;
import com.rin.kanban.mapper.RatingMapper;
import com.rin.kanban.repository.OrderProductsRepository;
import com.rin.kanban.repository.OrderRepository;
import com.rin.kanban.repository.RatingRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RatingService {
    RatingRepository ratingRepository;
    RatingMapper ratingMapper;
    OrderProductsRepository orderProductsRepository;
    OrderRepository orderRepository;

    public RatingResponse createRating(RatingRequest ratingRequest) {
        String userId = getAuthenticatedUserId();

        // Kiểm tra rating đã tồn tại
        if (ratingRepository.findByUserIdAndOrderIdAndSubProductId(userId, ratingRequest.getOrderId(), ratingRequest.getSubProductId()).isPresent()) {
            throw new AppException(ErrorCode.RATING_EXISTED);
        }

        // Kiểm tra order
        Order order = orderRepository.findById(ratingRequest.getOrderId())
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));

        if (!order.getUserId().equals(userId) || !order.getIsComplete()) {
            throw new AppException(ErrorCode.CAN_NOT_RATING);
        }

        // Kiểm tra sản phẩm
        OrderProduct orderProduct = orderProductsRepository.findById(ratingRequest.getSubProductId())
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

        if (orderProduct.getIsRating()) {
            throw new AppException(ErrorCode.CAN_NOT_RATING);
        }

        // Tạo và lưu rating
        Rating rating = ratingMapper.toRating(ratingRequest);
        rating.setUserId(userId);
        rating = ratingRepository.save(rating);

        return ratingMapper.toRatingResponse(rating);
    }

    public RatingResponse updateRating(RatingRequest ratingRequest) {
        String userId = getAuthenticatedUserId();

        // Tìm rating
        Rating rating = ratingRepository.findByUserIdAndOrderIdAndSubProductId(userId, ratingRequest.getOrderId(), ratingRequest.getSubProductId())
                .orElseThrow(() -> new AppException(ErrorCode.RATING_NOT_FOUND));

        // Cập nhật rating
        ratingMapper.updateRating(rating, ratingRequest);
        rating = ratingRepository.save(rating);

        return ratingMapper.toRatingResponse(rating);
    }

    private String getAuthenticatedUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication.getName() == null) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }
        return authentication.getName();
    }


}
