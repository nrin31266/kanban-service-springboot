package com.rin.kanban.service;

import com.rin.kanban.dto.PageResponse;
import com.rin.kanban.dto.request.*;
import com.rin.kanban.dto.response.*;
import com.rin.kanban.entity.Category;
import com.rin.kanban.entity.Product;
import com.rin.kanban.entity.SubProduct;
import com.rin.kanban.entity.Supplier;
import com.rin.kanban.exception.AppException;
import com.rin.kanban.exception.ErrorCode;
import com.rin.kanban.mapper.CategoryMapper;
import com.rin.kanban.mapper.ProductMapper;
import com.rin.kanban.mapper.SuppliersMapper;
import com.rin.kanban.pojo.ProductResult;
import com.rin.kanban.pojo.RatingResult;
import com.rin.kanban.repository.CategoryRepository;
import com.rin.kanban.repository.ProductRepository;
import com.rin.kanban.repository.SubProductRepository;
import com.rin.kanban.repository.SuppliersRepository;
import com.rin.kanban.repository.custom.OrderCustomRepository;
import com.rin.kanban.repository.custom.ProductCustomRepository;
import com.rin.kanban.repository.custom.RatingCustomRepository;
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
    OrderCustomRepository orderCustomRepository;
    RatingCustomRepository ratingCustomRepository;

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
        Product product = productRepository.findById(productId).orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));
        return convertProductResponse(product);

    }

    public PageResponse<ProductResponse> getProductsByFilterValues(FilterProductsRequest filterProductsRequest, int page, int size) {
        Page<Product> pageData = productCustomRepository.searchProducts(filterProductsRequest, page, size);

        List<ProductResponse> productResponses = pageData.getContent().stream().map(this::convertProductResponse).collect(Collectors.toList());

        return PageResponse.<ProductResponse>builder()
                .pageSize(pageData.getNumberOfElements())
                .currentPage(pageData.getNumber() + 1)
                .totalPages(pageData.getTotalPages())
                .totalElements(pageData.getTotalElements())
                .data(productResponses)
                .build();
    }

    private ProductResponse convertProductResponse(Product product) {
        ProductResponse productResponse = productMapper.toProductResponse(product);
        // Truy vấn tất cả danh mục cần thiết chỉ một lần
        if (productResponse.getCategoryIds() != null && !productResponse.getCategoryIds().isEmpty()) {
            List<String> categoryIds = product.getCategoryIds();
            List<Category> categories = categoryRepository.findAllById(categoryIds); // Truy vấn tất cả các category theo id
            List<CategoryResponse> categoryResponses = categories.stream()
                    .map(categoryMapper::toCategoryResponse)
                    .collect(Collectors.toList());
            productResponse.setCategoryResponse(categoryResponses);
        }

        // Truy vấn nhà cung cấp một lần
        if (productResponse.getSupplierId() != null) {
            Supplier supplier = suppliersRepository.findById(productResponse.getSupplierId()).orElse(null);
            if (supplier != null) {
                productResponse.setSupplierResponse(suppliersMapper.toSupplierResponse(supplier));
            }
        }
        productResponse.setTotalSold(orderCustomRepository.getSoldCountByProductId(product.getId()));
        RatingResult ratingResult = ratingCustomRepository.getRatingByProductId(product.getId());
        productResponse.setCountRating(ratingResult.getCountRating());
        productResponse.setAverageRating(ratingResult.getAverageRating());
        productResponse.setMinPrice(getMinPrice(product.getId()));
        productResponse.setMaxPrice(getMaxPrice(product.getId()));
        return productResponse;
    }

//    public List<ProductResponse> getProducts(int page, int size) {
//        Sort sort = Sort.by(Sort.Direction.DESC, "updatedAt");
//        Pageable pageable = PageRequest.of(page - 1, size, sort);
//        Page<Product> pageData = productRepository.findAllProducts(pageable);
//        return pageData.getContent().stream().map(productMapper::toProductResponse).collect(Collectors.toList());
//    }

    public PageResponse<ProductResponse> getProductsPagination(int page, int size) {
        Sort sort = Sort.by(Sort.Direction.DESC, "updatedAt");
        Pageable pageable = PageRequest.of(page - 1, size, sort);
        Page<Product> pageData = productRepository.findAllProducts(pageable);
        return getSubProductsByPage(pageData);
    }



    public PageResponse<ProductResponse> getProductsPaginationAndTitle(int page, int size, String title) {
        log.info(title);
        Sort sort = Sort.by(Sort.Direction.DESC, "updatedAt");
        Pageable pageable = PageRequest.of(page - 1, size, sort);
        Page<Product> pageData = productRepository.findAllBySlugContaining(title, pageable);

        return getSubProductsByPage(pageData);
    }

    private PageResponse<ProductResponse> getSubProductsByPage(Page<Product> pageData) {

        List<ProductResponse> productResponses = pageData.getContent().stream().map(this::convertProductResponse).collect(Collectors.toList());
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
        List<String> ids = productCustomRepository.getTopSoldProducts();
        List<Product> products = productRepository.findAllById(ids);
        // Sắp xếp lại sản phẩm theo thứ tự của ids
        Map<String, Product> productMap = products.stream()
                .collect(Collectors.toMap(Product::getId, product -> product));

        return ids.stream()
                .map(productMap::get)
                .filter(Objects::nonNull) // Lọc sản phẩm không tồn tại (nếu có)
                .map(this::convertProductResponse)
                .collect(Collectors.toList());
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
            return relatedProducts.stream().map(this::convertProductResponse).collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    public ProductResult test(String productId){
        return productCustomRepository.getProductById(productId);
    }


}
