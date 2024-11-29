package com.rin.kanban.repository.custom;

import com.rin.kanban.pojo.RatingResult;

public interface RatingCustomRepository {
    RatingResult getRatingByProductId(String productId);
}
