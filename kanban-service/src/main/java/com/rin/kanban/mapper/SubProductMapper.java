package com.rin.kanban.mapper;

import com.rin.kanban.dto.request.SubProductRequest;
import com.rin.kanban.dto.request.UpdateSubProductRequest;
import com.rin.kanban.dto.response.SubProductResponse;
import com.rin.kanban.entity.SubProduct;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface SubProductMapper {

    SubProduct toSubProduct(SubProductRequest subProductRequest);
    SubProductResponse toSubProductResponse(SubProduct subProduct);
    void updateSubProduct(@MappingTarget SubProduct subProduct, UpdateSubProductRequest updateSubProductRequest);
//    @Mapping(target = "value", source = "id")
//    @Mapping(target = "name", source = "id")
//    SelectResponse toSelectResponse(SubProduct subProduct);
}
