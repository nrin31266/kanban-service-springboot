package com.rin.kanban.service;

import com.rin.kanban.dto.PageResponse;
import com.rin.kanban.dto.request.SoftDeleteRequest;
import com.rin.kanban.dto.request.SubProductRequest;
import com.rin.kanban.dto.request.UpdateSubProductRequest;
import com.rin.kanban.dto.response.PromotionResponse;
import com.rin.kanban.dto.response.SelectDataResponse;
import com.rin.kanban.dto.response.SubProductResponse;
import com.rin.kanban.entity.Product;
import com.rin.kanban.entity.SubProduct;
import com.rin.kanban.exception.AppException;
import com.rin.kanban.exception.ErrorCode;
import com.rin.kanban.mapper.SubProductMapper;
import com.rin.kanban.repository.ProductRepository;
import com.rin.kanban.repository.SubProductRepository;
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

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SubProductService {
    SubProductRepository subProductRepository;
    ProductRepository productRepository;
    SubProductMapper subProductMapper;

    public SubProductResponse createSubProduct(SubProductRequest subProductRequest) {
        SubProduct subProduct = subProductMapper.toSubProduct(subProductRequest);
        String productIdConfirmed = productRepository.findById(subProductRequest.getProductId()).orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND)).getId();
        subProduct.setProductId(productIdConfirmed);
        return subProductMapper.toSubProductResponse(subProductRepository.save(subProduct));
    }

    public List<SelectDataResponse> getFilterValues() {
        return null;
    }

    public List<SubProductResponse> getSubProductsByProductId(String productId) {
        List<SubProduct> subProducts = subProductRepository.findAllByProductIdAndIsDeletedNullOrFalse(productId);
        return subProducts.stream().map(subProductMapper::toSubProductResponse).collect(Collectors.toList());
    }

    @Transactional
    public List<SubProductResponse> softDeleteProduct(SoftDeleteRequest request) {
        List<SubProduct> subProductsToDelete = new ArrayList<>();

        for (String productId : request.getIds()) {
            SubProduct subProduct = subProductRepository.findById(productId).orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));
            subProduct.setDeleted(true);
            subProductsToDelete.add(subProduct);
        }
        return subProductRepository.saveAll(subProductsToDelete).stream().map(subProductMapper::toSubProductResponse).collect(Collectors.toList());
    }

    public SubProductResponse updateSubProduct(UpdateSubProductRequest request, String subProductId) {
        SubProduct subProduct = subProductRepository.findById(subProductId).orElseThrow(() -> new AppException(ErrorCode.SUB_PRODUCT_NOT_FOUND));
        log.info("Update sub product: {}", request.toString());
        subProductMapper.updateSubProduct(subProduct, request);
        return subProductMapper.toSubProductResponse(subProductRepository.save(subProduct));
    }

    public PageResponse<SubProductResponse> getSubProducts(int page, int size) {
        Sort sort = Sort.by(Sort.Direction.DESC, "updatedAt");
        Pageable pageable = PageRequest.of(page - 1, size, sort);
        Page<SubProduct> pageData = subProductRepository.findSubProducts(pageable);

        return PageResponse.<SubProductResponse>builder()
                .totalElements(pageData.getTotalElements())
                .totalPages(pageData.getTotalPages())
                .currentPage(page)
                .pageSize(pageData.getNumberOfElements())
                .data(pageData.getContent().stream().map(subProductMapper::toSubProductResponse).toList())
                .build();
    }

}
