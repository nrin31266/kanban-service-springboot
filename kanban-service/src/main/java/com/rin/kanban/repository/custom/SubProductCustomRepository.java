package com.rin.kanban.repository.custom;

import com.rin.kanban.entity.SubProduct;

import java.util.Optional;

public interface SubProductCustomRepository {
    Optional<SubProduct> findMaxPriceSubProduct(String productId);
    Optional<SubProduct> findMinPriceSubProduct(String productId);
}
