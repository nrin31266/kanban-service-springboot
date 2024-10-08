package com.rin.kanban.service;

import com.rin.kanban.dto.request.SubProductRequest;
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
import org.springframework.stereotype.Service;

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
        Product product = productRepository.findById(subProductRequest.getProductId()).orElseThrow(()-> new AppException(ErrorCode.PRODUCT_NOT_FOUND));
        subProduct.setProduct(product);
        return subProductMapper.toSubProductResponse(subProductRepository.save(subProduct));
    }

}
