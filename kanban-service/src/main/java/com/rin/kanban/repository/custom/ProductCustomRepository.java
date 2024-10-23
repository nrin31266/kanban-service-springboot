package com.rin.kanban.repository.custom;

import com.rin.kanban.dto.request.ProductsFilterValuesRequest;
import com.rin.kanban.entity.Product;

import java.awt.print.Pageable;
import java.util.List;

public interface ProductCustomRepository {
    List<Product> findAllByFilterValues(ProductsFilterValuesRequest productsFilterValuesRequest, Pageable pageable);

}
