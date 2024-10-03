package com.rin.kanban.service;

import com.rin.kanban.dto.request.ProductRequest;
import com.rin.kanban.dto.response.ProductResponse;
import com.rin.kanban.entity.Category;
import com.rin.kanban.entity.Product;
import com.rin.kanban.exception.AppException;
import com.rin.kanban.exception.ErrorCode;
import com.rin.kanban.mapper.ProductMapper;
import com.rin.kanban.repository.CategoryRepository;
import com.rin.kanban.repository.ProductRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashSet;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ProductService {
    ProductRepository productRepository;
    ProductMapper productMapper;
    CategoryRepository categoryRepository;

    public ProductResponse createProduct(ProductRequest productRequest) {
        Product product = productMapper.toProduct(productRequest);
        HashSet<Category> categories = new HashSet<>();
        productRequest.getCategories().forEach((categoryId->{
            categories.add(categoryRepository.findById(categoryId).orElseThrow(()->new AppException(ErrorCode.CATEGORY_NOT_FOUND)));
        }));
        product.setCategories(categories);
        return productMapper.toProductResponse(productRepository.save(product));
    }
}
