package com.rin.kanban.repository.custom;

import com.rin.kanban.dto.request.FilterProductsRequest;
import com.rin.kanban.dto.response.ProductResponse;
import com.rin.kanban.entity.Product;
import com.rin.kanban.pojo.BestSellerResult;
import com.rin.kanban.pojo.ProductResult;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

public interface ProductCustomRepository {
    List<String> getTopSoldProducts();
//    Optional<ProductResponse> getProductById(String productId);

    Page<ProductResponse> searchProductsV2(FilterProductsRequest filterRequest, int page, int size);
}
