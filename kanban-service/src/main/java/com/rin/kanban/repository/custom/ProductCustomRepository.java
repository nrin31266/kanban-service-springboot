package com.rin.kanban.repository.custom;

import com.rin.kanban.dto.request.ProductsFilterValuesRequest;
import com.rin.kanban.entity.Product;
import org.springframework.data.domain.Page;

public interface ProductCustomRepository {
    Page<Product> findAllByFilterValues(ProductsFilterValuesRequest productsFilterValuesRequest);
}
