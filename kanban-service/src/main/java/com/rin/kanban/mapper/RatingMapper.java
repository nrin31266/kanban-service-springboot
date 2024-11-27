package com.rin.kanban.mapper;

import com.rin.kanban.dto.request.RatingRequest;
import com.rin.kanban.dto.response.RatingResponse;
import com.rin.kanban.entity.Rating;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface RatingMapper {
    Rating toRating(RatingRequest request);
    RatingResponse toRatingResponse(Rating rating);
    void updateRating(@MappingTarget Rating rating, RatingRequest request);
}
