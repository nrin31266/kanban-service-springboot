package com.rin.kanban.service;

import com.rin.kanban.dto.PageResponse;
import com.rin.kanban.dto.request.ProductRequest;
import com.rin.kanban.dto.request.ProductsFilterValuesRequest;
import com.rin.kanban.dto.request.SoftDeleteRequest;
import com.rin.kanban.dto.response.ProductHasSubProductsResponse;
import com.rin.kanban.dto.response.ProductResponse;
import com.rin.kanban.dto.response.SubProductResponse;
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
import com.rin.kanban.repository.custom.ProductCustomRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
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
    ProductCustomRepository productCustomRepository;

    public ProductResponse createProduct(ProductRequest productRequest) {
        Product product = productMapper.toProduct(productRequest);
        Set<String> categoryIdsConfirmed = new HashSet<>();
        productRequest.getCategoryIds().forEach((categoryId -> {
            categoryIdsConfirmed.add(categoryRepository.findById(categoryId).orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND)).getId());
        }));
        product.setCategoryIds(categoryIdsConfirmed);
        return productMapper.toProductResponse(productRepository.save(product));
    }

    public Boolean deleteProduct(String productId) {
        productRepository.findById(productId).orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));
        try {
            subProductRepository.deleteAllByProductId(productId);
            productRepository.deleteById(productId);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public ProductResponse getProduct(String productId) {
        return productMapper.toProductResponse(productRepository.findById(productId).orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND)));
    }

    public List<ProductResponse> getProducts() {
        List<Product> products = productRepository.findAll();
        return products.stream().map(productMapper::toProductResponse).collect(Collectors.toList());
    }
//    public List<ProductHasSubProductsResponse> getProductsData() {
//        List<Product> products = productRepository.findAllByIsDeletedIsNullOrIsDeletedIsFalse();
//        return products.parallelStream().map(product -> {
//            ProductHasSubProductsResponse response = productMapper.toProductHasSubProductsResponse(product);
//            List<SubProduct> subProducts = subProductRepository.findByProductId(product.getId());
//            if (!subProducts.isEmpty())
//                response.setSubProductResponse(subProducts.stream().map(subProductMapper::toSubProductResponse).toList());
//            return response;
//        }).collect(Collectors.toList());
//    }

    public PageResponse<ProductHasSubProductsResponse> getProductsWithPageAndSize(int page, int size) {
        Sort sort = Sort.by("updatedAt").descending();
        Pageable pageable = PageRequest.of(page - 1, size, sort);
        Page<Product> pageData = productRepository.findAllByIsDeletedIsNullOrFalse(pageable);

        return getSubProductsByPage(pageData);
    }

    public PageResponse<ProductHasSubProductsResponse> getProductsWithPageAndSizeAndTitle(int page, int size, String title) {
        log.info(title);
        Sort sort = Sort.by("updatedAt").descending();
        Pageable pageable = PageRequest.of(page - 1, size, sort);
        Page<Product> pageData = productRepository.findAllBySlugContaining(title, pageable);

        return getSubProductsByPage(pageData);
    }

    private PageResponse<ProductHasSubProductsResponse> getSubProductsByPage(Page<Product> pageData) {
        List<ProductHasSubProductsResponse> response = pageData.getContent().stream().map((product) -> {
            List<SubProduct> subProducts = subProductRepository.findAllByProductIdAndIsDeletedNullOrFalse(product.getId());
            ProductHasSubProductsResponse productHasSubProductsResponse = productMapper.toProductHasSubProductsResponse(product);
            if (product.getCategoryIds() != null && !product.getCategoryIds().isEmpty()) {
                Set<Category> categories = new HashSet<>();
                product.getCategoryIds().forEach((categoryId) -> {
                    Category category = categoryRepository.findById(categoryId).orElse(null);
                    categories.add(category);
                });
                productHasSubProductsResponse.setCategories(categories);
            }
            if (!subProducts.isEmpty()) {
                List<SubProductResponse> subProductResponses = subProducts.stream().map(subProductMapper::toSubProductResponse).toList();
                productHasSubProductsResponse.setSubProductResponse(subProductResponses);
            }
            return productHasSubProductsResponse;
        }).toList();

        return PageResponse.<ProductHasSubProductsResponse>builder()
                .pageSize(pageData.getSize())
                .currentPage(pageData.getNumber() + 1)
                .totalPages(pageData.getTotalPages())
                .totalElements(pageData.getTotalElements())
                .data(response)
                .build();
    }

    public ProductResponse updateProduct(String productId, ProductRequest productRequest) {
        Product product = productRepository.findById(productId).orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));
        productMapper.updateProduct(product, productRequest);
        if (productRequest.getCategoryIds() != null) {
            Set<String> categoryIdConfirm = new HashSet<>();
            productRequest.getCategoryIds().forEach((categoryId -> {
                Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));
                categoryIdConfirm.add(category.getId());
            }));
            product.setCategoryIds(categoryIdConfirm);
        }
        return productMapper.toProductResponse(productRepository.save(product));
    }

    @Transactional
    public void softDeleteProduct(SoftDeleteRequest request) {
        List<Product> productsToDelete = new ArrayList<>();
        for (String productId : request.getIds()) {
            Product product = productRepository.findById(productId).orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));
            product.setIsDeleted(true);
            productsToDelete.add(product);
        }
        productRepository.saveAll(productsToDelete);
    }

    public PageResponse<ProductHasSubProductsResponse> getProductsByFilterValues(ProductsFilterValuesRequest request) {
        Page<Product> productPage = productCustomRepository.findAllByFilterValues(request);
        log.info(productPage.getContent().toString());
        return getSubProductsByPage(productPage);
    }
}
