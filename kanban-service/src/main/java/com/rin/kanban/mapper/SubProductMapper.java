package com.rin.kanban.mapper;

import com.rin.kanban.dto.request.SubProductRequest;
import com.rin.kanban.dto.response.SubProductResponse;
import com.rin.kanban.entity.SubProduct;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SubProductMapper {

    SubProduct toSubProduct(SubProductRequest subProductRequest);
    SubProductResponse toSubProductResponse(SubProduct subProduct);
//    @Mapping(target = "value", source = "id")
//    @Mapping(target = "name", source = "id")
//    SelectResponse toSelectResponse(SubProduct subProduct);
}
