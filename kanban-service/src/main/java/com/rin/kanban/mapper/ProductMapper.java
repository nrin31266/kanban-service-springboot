package com.rin.kanban.mapper;

import com.rin.kanban.dto.request.ProductRequest;
import com.rin.kanban.dto.response.ProductResponse;
import com.rin.kanban.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    Product toProduct(ProductRequest request);
    ProductResponse toProductResponse(Product product);
    void updateProduct(@MappingTarget Product product, ProductRequest request);
}
