package com.rin.kanban.repository;

import com.rin.kanban.entity.Promotion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;


import java.util.List;
import java.util.Optional;

public interface PromotionRepository extends MongoRepository<Promotion, String> {
    Optional<Promotion> findByCode(String promotionId);
    @Query("{$or: [{isDeleted: false}, {isDeleted:  null}]}")
    Page<Promotion> findAllPaginatedPromotions(Pageable pageable);
}
