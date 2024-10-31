package com.rin.kanban.repository;

import com.rin.kanban.entity.Promotion;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PromotionRepository extends MongoRepository<Promotion, String> {
}
