package com.rin.kanban.service;

import com.rin.kanban.dto.PageResponse;
import com.rin.kanban.dto.httpclient.UserProfileResponse;
import com.rin.kanban.dto.request.RatingRequest;
import com.rin.kanban.dto.request.ReplyRatingRequest;
import com.rin.kanban.dto.response.OrderResponse;
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
import com.rin.kanban.repository.httpclient.ProfileClient;
import com.rin.kanban.util.DateTimeFormatter;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RatingService {
    RatingRepository ratingRepository;
    RatingMapper ratingMapper;
    OrderProductsRepository orderProductsRepository;
    OrderRepository orderRepository;
    ProfileClient profileClient;
    DateTimeFormatter  dateTimeFormatter;

    @Transactional
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
        OrderProduct orderProduct = orderProductsRepository.findById(ratingRequest.getOrderProductId())
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

        if (Boolean.TRUE.equals(orderProduct.getIsRating())) {
            throw new AppException(ErrorCode.CAN_NOT_RATING);
        }
        orderProduct.setIsRating(true);
        orderProductsRepository.save(orderProduct);

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

    public RatingResponse replyRating(ReplyRatingRequest request, String ratingId) {
        Rating rating = ratingRepository.findById(ratingId).orElseThrow(() -> new AppException(ErrorCode.RATING_NOT_FOUND));
        rating.setReply(request.getReply());
        ratingRepository.save(rating);
        return ratingMapper.toRatingResponse(rating);
    }


    public PageResponse<RatingResponse> getRatings(String productId, int page, int size) {
        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        Pageable pageable = PageRequest.of(page -1, size, sort);
        Page<Rating> pageData = ratingRepository.findByProductId(productId, pageable);

        List<RatingResponse> ratingResponses = pageData.getContent().stream().map((rating)->{
            RatingResponse ratingResponse = ratingMapper.toRatingResponse(rating);
            UserProfileResponse userProfileResponse = profileClient.getProfile(rating.getUserId()).getResult();
            if(userProfileResponse != null) {
                ratingResponse.setAvatar(userProfileResponse.getAvatar() != null ? userProfileResponse.getAvatar() : null);
                ratingResponse.setName(userProfileResponse.getName());
            }
            ratingResponse.setCreated(dateTimeFormatter.formatDate(rating.getCreatedAt()));
            ratingResponse.setUpdated(dateTimeFormatter.formatDate(rating.getUpdatedAt()));
            return ratingResponse;
        }).toList();

        return PageResponse.<RatingResponse>builder()
                .pageSize(pageData.getSize())
                .currentPage(pageData.getNumber() + 1)
                .totalPages(pageData.getTotalPages())
                .totalElements(pageData.getTotalElements())
                .data(ratingResponses)
                .build();
    }
}
