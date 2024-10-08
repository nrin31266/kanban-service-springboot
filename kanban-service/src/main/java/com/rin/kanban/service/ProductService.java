package com.rin.kanban.service;

import com.rin.kanban.dto.request.ProductRequest;
import com.rin.kanban.dto.response.ProductHasSubProductsResponse;
import com.rin.kanban.dto.response.ProductResponse;
import com.rin.kanban.entity.Category;
import com.rin.kanban.entity.Product;
import com.rin.kanban.entity.SubProduct;
import com.rin.kanban.exception.AppException;
import com.rin.kanban.exception.ErrorCode;
import com.rin.kanban.mapper.ProductMapper;
import com.rin.kanban.mapper.SubProductMapper;
import com.rin.kanban.repository.CategoryRepository;
import com.rin.kanban.repository.ProductRepository;
import com.rin.kanban.repository.SubProductRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ProductService {
    ProductRepository productRepository;
    ProductMapper productMapper;
    CategoryRepository categoryRepository;
    SubProductRepository subProductRepository;
    SubProductMapper subProductMapper;
    public ProductResponse createProduct(ProductRequest productRequest) {
        Product product = productMapper.toProduct(productRequest);
        HashSet<Category> categories = new HashSet<>();
        productRequest.getCategories().forEach((categoryId->{
            categories.add(categoryRepository.findById(categoryId).orElseThrow(()->new AppException(ErrorCode.CATEGORY_NOT_FOUND)));
        }));
        product.setCategories(categories);
        return productMapper.toProductResponse(productRepository.save(product));
    }

    public Boolean deleteProduct(String productId) {
        try {
            productRepository.deleteById(productId);
            return true;
        }catch (Exception e) {
            return false;
        }
    }
    public ProductResponse getProduct(String productId) {
        return productMapper.toProductResponse(productRepository.findById(productId).orElseThrow());
    }
    public List<ProductHasSubProductsResponse> getProducts() {
        List<Product> products = productRepository.findAll();
        List<ProductHasSubProductsResponse> responses = products.parallelStream().map(product -> {
            ProductHasSubProductsResponse response = productMapper.toProductHasSubProductsResponse(product);
            List<SubProduct> subProducts = subProductRepository.findByProductId(product.getId());

            if(!subProducts.isEmpty())
                response.setSubProductResponse(subProducts.stream().map(subProductMapper::toSubProductResponse).toList());
            return response;
        }).collect(Collectors.toList());
        return responses;
    }
    public ProductResponse updateProduct(String productId, ProductRequest productRequest) {
        Product product = productRepository.findById(productId).orElseThrow(() ->new AppException(ErrorCode.PRODUCT_NOT_FOUND));
        productMapper.updateProduct(product, productRequest);

        if(productRequest.getCategories() != null) {
            HashSet<Category> categories = new HashSet<>();
            productRequest.getCategories().forEach((categoryId->{
                Category category = categoryRepository.findById(categoryId).orElseThrow(()->new AppException(ErrorCode.CATEGORY_NOT_FOUND));
                categories.add(category);
            }));
            product.setCategories(categories);
        }
        return productMapper.toProductResponse(productRepository.save(product));
    }
}
