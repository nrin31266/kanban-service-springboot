package com.rin.kanban.repository.custom;

import com.rin.kanban.dto.request.FilterProductsRequest;
import com.rin.kanban.entity.Product;
import com.rin.kanban.pojo.BestSellerResult;
import com.rin.kanban.pojo.ProductResult;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ProductCustomRepository {
    Page<Product> searchProducts(FilterProductsRequest filterRequest, int page, int size);
    List<String> getTopSoldProducts();
    ProductResult getProductById(String productId);
}
