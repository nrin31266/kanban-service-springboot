package com.rin.kanban.repository.custom;

import com.rin.kanban.entity.SubProduct;

import java.util.Optional;

public interface SubProductCustomRepository {
    Optional<SubProduct> findMaxPrice(String productId);
    Optional<SubProduct> findMinPrice(String productId);
    Optional<SubProduct> findMaxPrice();
    Optional<SubProduct> findMinPrice();
}
