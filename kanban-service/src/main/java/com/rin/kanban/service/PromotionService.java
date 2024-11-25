package com.rin.kanban.service;

import com.rin.kanban.constant.DiscountType;
import com.rin.kanban.dto.PageResponse;
import com.rin.kanban.dto.request.DiscountCodeRequest;
import com.rin.kanban.dto.request.CreatePromotionRequest;
import com.rin.kanban.dto.request.SoftDeleteRequest;
import com.rin.kanban.dto.request.UpdatePromotionRequest;
import com.rin.kanban.dto.response.DiscountCodeResponse;
import com.rin.kanban.dto.response.PromotionResponse;
import com.rin.kanban.entity.Promotion;
import com.rin.kanban.exception.AppException;
import com.rin.kanban.exception.ErrorCode;
import com.rin.kanban.mapper.PromotionMapper;
import com.rin.kanban.repository.PromotionRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class PromotionService {
    PromotionRepository promotionRepository;
    PromotionMapper promotionMapper;


    public PromotionResponse createPromotion(CreatePromotionRequest request) {
        if (promotionRepository.findByCode(request.getCode()).isPresent()) {
            throw new AppException(ErrorCode.PROMOTION_EXISTED);
        }
        Promotion promotion = promotionMapper.toPromotion(request);
        try {
            promotion.setDiscountType(DiscountType.valueOf(request.getDiscountType().toUpperCase()));
        } catch (IllegalArgumentException exception) {
            throw new AppException(ErrorCode.DISCOUNT_INVALID);
        }
        return promotionMapper.toPromotionResponse(promotionRepository.save(promotion));
    }

    public PromotionResponse updatePromotion(UpdatePromotionRequest request, String promotionId) {
        Promotion promotion = promotionRepository.findById(promotionId).orElseThrow(() -> new AppException((ErrorCode.PROMOTION_NOT_FOUND)));
        promotionMapper.updatePromotion(promotion, request);
        if (request.getDiscountType() != null) {
            try {
                promotion.setDiscountType(DiscountType.valueOf(request.getDiscountType().toUpperCase()));
            } catch (IllegalArgumentException exception) {
                throw new AppException(ErrorCode.DISCOUNT_INVALID);
            }
        }
        return promotionMapper.toPromotionResponse(promotionRepository.save(promotion));
    }

    public PromotionResponse softDeletePromotion(String promotionId) {
        Promotion promotion = promotionRepository.findById(promotionId).orElseThrow(() -> new AppException((ErrorCode.PROMOTION_NOT_FOUND)));
        promotion.setIsDeleted(true);
        return promotionMapper.toPromotionResponse(promotionRepository.save(promotion));
    }

    @Transactional
    public List<PromotionResponse> softDeletePromotions(SoftDeleteRequest promotionIds) {
        return promotionIds.getIds().stream().map(this::softDeletePromotion).collect(Collectors.toList());
    }

    public PageResponse<PromotionResponse> getPromotions(int page, int size) {
        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        Pageable pageable = PageRequest.of(page - 1, size, sort);
        Page<Promotion> pageData = promotionRepository.findAllPaginatedPromotions(pageable);
        List<PromotionResponse> promotionsResponse = pageData.getContent().stream().map(promotionMapper::toPromotionResponse).toList();
        return PageResponse.<PromotionResponse>builder()
                .totalElements(pageData.getTotalElements())
                .totalPages(pageData.getTotalPages())
                .currentPage(page)
                .pageSize(pageData.getNumberOfElements())
                .data(promotionsResponse)
                .build();
    }


    private void validateDiscountCode(Promotion promotion) {
        if (promotion.getQuantity() < 1) {
            throw new AppException(ErrorCode.PROMOTION_UN_STOCK);
        } else if (Instant.now().isBefore(promotion.getStart())) {
            throw new AppException(ErrorCode.DISCOUNT_NOT_YET_AVAILABLE);
        } else if (Instant.now().isAfter(promotion.getEnd())) {
            throw new AppException(ErrorCode.DISCOUNT_ENDED);
        }
    }

    public DiscountCodeResponse checkDiscountCode(DiscountCodeRequest request) {
        Optional<Promotion> optionalPromotion = promotionRepository.findByCode(request.getDiscountCode());
        DiscountCodeResponse response = new DiscountCodeResponse();

        if (optionalPromotion.isPresent()) {
            Promotion promotion = optionalPromotion.get();
            validateDiscountCode(promotion);

            response.setIsValid(true);
            response.setMessage("Ok");
            response.setPromotionResponse(promotionMapper.toPromotionResponse(promotion));
        } else {
            throw new AppException(ErrorCode.PROMOTION_NOT_FOUND);
        }
        return response;
    }


    public DiscountCodeResponse useDiscountCode(DiscountCodeRequest request) {
        DiscountCodeResponse response = checkDiscountCode(request);

        if (response.getIsValid()) {
            Promotion promotion = promotionRepository.findByCode(request.getDiscountCode()).orElseThrow(() -> new AppException(ErrorCode.PROMOTION_NOT_FOUND));
            promotion.setQuantity(promotion.getQuantity() - 1);
            promotion.setUsed((promotion.getUsed() != null) ? promotion.getUsed() + 1 : 1);
            promotionRepository.save(promotion);
        }

        return response;
    }


}
