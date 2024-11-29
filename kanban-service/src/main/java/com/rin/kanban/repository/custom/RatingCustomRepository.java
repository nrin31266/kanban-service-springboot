package com.rin.kanban.repository.custom;

import com.rin.kanban.pojo.BestSellerResult;
import com.rin.kanban.pojo.RatingResult;

import java.util.List;

public interface RatingCustomRepository {
    RatingResult getRatingByProductId(String productId);

}
