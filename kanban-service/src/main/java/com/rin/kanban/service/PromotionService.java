package com.rin.kanban.service;

import com.rin.kanban.constant.DiscountType;
import com.rin.kanban.dto.PageResponse;
import com.rin.kanban.dto.request.CheckDiscountCodeRequest;
import com.rin.kanban.dto.request.CreatePromotionRequest;
import com.rin.kanban.dto.request.SoftDeleteRequest;
import com.rin.kanban.dto.request.UpdatePromotionRequest;
import com.rin.kanban.dto.response.CheckDiscountCodeResponse;
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

    public CheckDiscountCodeResponse checkDiscountCode(CheckDiscountCodeRequest request) {
        Optional<Promotion> optionalPromotion = promotionRepository.findByCode(request.getDiscountCode());
        CheckDiscountCodeResponse response = new CheckDiscountCodeResponse();
        if(optionalPromotion.isPresent()){
            Promotion promotion = optionalPromotion.get();
            if(promotion.getQuantity()<1){
                response.setIsValid(false);
                response.setMessage("Un stock");
            }else if(Instant.now().isBefore(promotion.getStart())){
                response.setIsValid(false);
                response.setMessage("Discounts not yet available");
            }else if(Instant.now().isAfter(promotion.getEnd())){
                response.setIsValid(false);
                response.setMessage("Discounts ended");
            }else{
                response.setIsValid(true);
                response.setMessage("Ok");
            }
            response.setPromotionResponse(promotionMapper.toPromotionResponse(optionalPromotion.get()));
        }else{
            response.setIsValid(false);
        }
        return response;
    }

}
