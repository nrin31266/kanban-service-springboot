package com.rin.kanban.mapper;

import com.rin.kanban.dto.request.CreatePromotionRequest;
import com.rin.kanban.dto.response.PromotionResponse;
import com.rin.kanban.entity.Promotion;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PromotionMapper {
    @Mapping(target = "discountType", ignore = true)
    Promotion toPromotion(CreatePromotionRequest request);
    PromotionResponse toPromotionResponse(Promotion promotion);
}
