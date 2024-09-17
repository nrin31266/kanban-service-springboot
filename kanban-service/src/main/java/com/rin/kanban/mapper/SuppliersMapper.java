package com.rin.kanban.mapper;

import com.rin.kanban.dto.request.SupplierRequest;
import com.rin.kanban.dto.response.SupplierResponse;
import com.rin.kanban.entity.Supplier;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface SuppliersMapper {
    @Mapping(target = "categories", ignore = true)
    Supplier toSupplier(SupplierRequest request);
    @Mapping(target = "categories", ignore = true)
    void supplierUpdate(@MappingTarget Supplier supplier, SupplierRequest request);
    SupplierResponse toSupplierResponse(Supplier supplier);
}
