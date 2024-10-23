package com.rin.kanban.repository.custom;

import com.rin.kanban.dto.PageResponse;
import com.rin.kanban.dto.request.ProductsFilterValuesRequest;
import com.rin.kanban.dto.response.ProductHasSubProductsResponse;
import com.rin.kanban.entity.Product;
import org.springframework.data.domain.Page;

import java.awt.print.Pageable;
import java.util.List;

public interface ProductCustomRepository {
    Page<Product> findAllByFilterValues(ProductsFilterValuesRequest productsFilterValuesRequest);

}
