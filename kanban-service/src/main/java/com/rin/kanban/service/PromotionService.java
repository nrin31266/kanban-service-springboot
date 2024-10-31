package com.rin.kanban.service;

import com.rin.kanban.constant.DiscountType;
import com.rin.kanban.dto.request.CreatePromotionRequest;
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
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class PromotionService {
    PromotionRepository promotionRepository;
    PromotionMapper promotionMapper;
    public PromotionResponse createPromotion(CreatePromotionRequest request) {
        if(promotionRepository.findByCode(request.getCode()).isPresent()) {
            throw new AppException(ErrorCode.PROMOTION_EXISTED);
        }
        Promotion promotion = promotionMapper.toPromotion(request);
        if(request.getDiscountType()){
            promotion.setDiscountType(DiscountType.FIXED_AMOUNT);
        }else{
            promotion.setDiscountType(DiscountType.PERCENTAGE);
        }
        return promotionMapper.toPromotionResponse(promotionRepository.save(promotion));
    }
}
