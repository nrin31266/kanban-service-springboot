package com.rin.kanban.controller;

import com.rin.kanban.dto.ApiResponse;
import com.rin.kanban.dto.request.RatingRequest;
import com.rin.kanban.dto.response.RatingResponse;
import com.rin.kanban.service.RatingService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/rating")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RatingController {
    RatingService ratingService;

    @PostMapping
    public ApiResponse<RatingResponse> createRating(@RequestBody RatingRequest ratingRequest) {
        return ApiResponse.<RatingResponse>builder()
                .result(ratingService.createRating(ratingRequest))
                .build();
    }

    @PutMapping
    public ApiResponse<RatingResponse> updateRating(@RequestBody RatingRequest ratingRequest) {
        return ApiResponse.<RatingResponse>builder()
                .result(ratingService.updateRating(ratingRequest))
                .build();
    }


}
