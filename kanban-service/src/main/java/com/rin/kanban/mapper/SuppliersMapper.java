package com.rin.kanban.mapper;

import com.rin.kanban.dto.request.SuppliersRequest;
import com.rin.kanban.dto.response.SuppliersResponse;
import com.rin.kanban.entity.Suppliers;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SuppliersMapper {
    @Mapping(target = "categories", ignore = true)
    Suppliers toSuppliers(SuppliersRequest request);

    SuppliersResponse toSuppliersResponse(Suppliers suppliers);
}
