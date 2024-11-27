package com.rin.kanban.repository;

import com.rin.kanban.entity.Rating;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface RatingRepository extends MongoRepository<Rating, String> {
    Optional<Rating> findByUserIdAndOrderIdAndSubProductId(String userId, String orderId, String subProductId);
}
