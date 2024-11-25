package com.rin.kanban.service;

import com.rin.kanban.dto.PageResponse;
import com.rin.kanban.dto.request.*;
import com.rin.kanban.dto.response.*;
import com.rin.kanban.entity.Product;
import com.rin.kanban.entity.SubProduct;
import com.rin.kanban.exception.AppException;
import com.rin.kanban.exception.ErrorCode;
import com.rin.kanban.mapper.CategoryMapper;
import com.rin.kanban.mapper.ProductMapper;
import com.rin.kanban.mapper.SuppliersMapper;
import com.rin.kanban.repository.CategoryRepository;
import com.rin.kanban.repository.ProductRepository;
import com.rin.kanban.repository.SubProductRepository;
import com.rin.kanban.repository.SuppliersRepository;
import com.rin.kanban.repository.custom.ProductCustomRepository;
import com.rin.kanban.repository.custom.SubProductCustomRepository;
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

import java.math.BigDecimal;
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
    ProductCustomRepository productCustomRepository;
    SubProductCustomRepository subProductCustomRepository;
    CategoryMapper categoryMapper;
    SuppliersMapper suppliersMapper;
    SuppliersRepository suppliersRepository;

    public ProductResponse createProduct(ProductRequest productRequest) {
        Product product = productMapper.toProduct(productRequest);
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
        ProductResponse productResponse = productMapper.toProductResponse(productRepository.findById(productId).orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND)));
        if (productResponse.getCategoryIds() != null) {
            List<CategoryResponse> categories = productResponse.getCategoryIds().stream().map((categoryId) -> categoryMapper.toCategoryResponse(categoryRepository.findById(categoryId).get())).collect(Collectors.toList());
            productResponse.setCategoryResponse(categories);
        }
        if (productResponse.getSupplierId() != null) {
            productResponse.setSupplierResponse(suppliersMapper.toSupplierResponse(suppliersRepository.findById(productResponse.getSupplierId()).get()));
        }
        return productResponse;
    }

    public List<ProductResponse> getProducts(int page, int size) {
        Sort sort = Sort.by(Sort.Direction.DESC, "updatedAt");
        Pageable pageable = PageRequest.of(page - 1, size, sort);
        Page<Product> pageData = productRepository.findAllProducts(pageable);
        return pageData.getContent().stream().map(productMapper::toProductResponse).collect(Collectors.toList());
    }

    public PageResponse<ProductResponse> getProductsPagination(int page, int size) {
        Sort sort = Sort.by(Sort.Direction.DESC, "updatedAt");
        Pageable pageable = PageRequest.of(page - 1, size, sort);
        Page<Product> pageData = productRepository.findAllProducts(pageable);
        return getSubProductsByPage(pageData);
    }

    public PageResponse<ProductResponse> getProductsByFilterValues(FilterProductsRequest filterProductsRequest, int page, int size) {
        Page<Product> pageData = productCustomRepository.searchProducts(filterProductsRequest, page, size);

        List<ProductResponse> productResponses = pageData.getContent().stream().map((product -> {
            ProductResponse productResponse = productMapper.toProductResponse(product);
            productResponse.setMinPrice(getMinPrice(product.getId()));
            productResponse.setMaxPrice(getMaxPrice(product.getId()));
            return productResponse;
        })).collect(Collectors.toList());

        return PageResponse.<ProductResponse>builder()
                .pageSize(pageData.getNumberOfElements())
                .currentPage(pageData.getNumber() + 1)
                .totalPages(pageData.getTotalPages())
                .totalElements(pageData.getTotalElements())
                .data(productResponses)
                .build();
    }

    public PageResponse<ProductResponse> getProductsPaginationAndTitle(int page, int size, String title) {
        log.info(title);
        Sort sort = Sort.by(Sort.Direction.DESC, "updatedAt");
        Pageable pageable = PageRequest.of(page - 1, size, sort);
        Page<Product> pageData = productRepository.findAllBySlugContaining(title, pageable);

        return getSubProductsByPage(pageData);
    }

    private PageResponse<ProductResponse> getSubProductsByPage(Page<Product> pageData) {

        List<ProductResponse> productResponses = pageData.getContent().stream().map((product) -> {
            ProductResponse productResponse = productMapper.toProductResponse(product);
            if (productResponse.getCategoryIds() != null) {
                List<CategoryResponse> categories = productResponse.getCategoryIds().stream().map((categoryId) -> categoryMapper.toCategoryResponse(categoryRepository.findById(categoryId).get())).collect(Collectors.toList());
                productResponse.setCategoryResponse(categories);
            }
            if (productResponse.getSupplierId() != null) {
                productResponse.setSupplierResponse(suppliersMapper.toSupplierResponse(suppliersRepository.findById(productResponse.getSupplierId()).get()));
            }
            return productResponse;
        }).collect(Collectors.toList());
        return PageResponse.<ProductResponse>builder()
                .pageSize(pageData.getSize())
                .currentPage(pageData.getNumber() + 1)
                .totalPages(pageData.getTotalPages())
                .totalElements(pageData.getTotalElements())
                .data(productResponses)
                .build();
    }

    public ProductResponse updateProduct(String productId, ProductRequest productRequest) {
        Product product = productRepository.findById(productId).orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));
        productMapper.updateProduct(product, productRequest);
        return productMapper.toProductResponse(productRepository.save(product));
    }

    @Transactional
    public void softDeleteProduct(SoftDeleteRequest request) {
        List<Product> productsToDelete = new ArrayList<>();
        for (String productId : request.getIds()) {
            Product product = productRepository.findById(productId).orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));
            product.setDeleted(true);
            productsToDelete.add(product);
        }
        productRepository.saveAll(productsToDelete);
    }

//    public PageResponse<ProductResponse> getProductsByFilterValues(ProductsFilterValuesRequest request) {
//        Page<Product> productPage = productCustomRepository.findAllByFilterValues(request);
//        log.info(productPage.getContent().toString());
//        return getSubProductsByPage(productPage);
//    }

    public List<ProductResponse> getBestsellerProducts() {
        //
        Sort sort = Sort.by(Sort.Direction.DESC, "updatedAt");
        Pageable pageable = PageRequest.of(0, 10, sort);
        Page<Product> pageData = productRepository.findAllProducts(pageable);
        return pageData.getContent().stream().map((product) -> {
            ProductResponse productResponse = productMapper.toProductResponse(product);
            productResponse.setMinPrice(getMinPrice(product.getId()));
            productResponse.setMaxPrice(getMaxPrice(product.getId()));
            return productResponse;
        }).collect(Collectors.toList());
    }

    private BigDecimal getMinPrice(String productId) {
        Optional<SubProduct> maxPriceSubProduct = subProductCustomRepository.findMinPrice(productId);
        return maxPriceSubProduct.map(SubProduct::getPrice).orElse(null);
    }

    private BigDecimal getMaxPrice(String productId) {
        Optional<SubProduct> minPriceSubProduct = subProductCustomRepository.findMaxPrice(productId);
        return minPriceSubProduct.map(SubProduct::getPrice).orElse(null);
    }

    public List<ProductResponse> getRelatedProducts(String productId) {
        Product product = productRepository.findById(productId).orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));
        if (product.getCategoryIds() != null) {
            Pageable pageable = PageRequest.of(0, 4, Sort.by(Sort.Direction.DESC, "updatedAt"));
            List<Product> relatedProducts = productRepository.findByCategoryIdsInAndNotDeleted(productId, product.getCategoryIds(), pageable);
            log.info(relatedProducts.toString());
            return relatedProducts.stream().map((p) -> {
                ProductResponse productResponse = productMapper.toProductResponse(p);
                productResponse.setMinPrice(getMinPrice(p.getId()));
                productResponse.setMaxPrice(getMaxPrice(p.getId()));
                return productResponse;
            }).collect(Collectors.toList());

        }
        return new ArrayList<>();
    }




}
