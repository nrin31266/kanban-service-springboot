package com.rin.kanban.repository;

import com.rin.kanban.entity.Promotion;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface PromotionRepository extends MongoRepository<Promotion, String> {
    Optional<Promotion> findByCode(String promotionId);
}
