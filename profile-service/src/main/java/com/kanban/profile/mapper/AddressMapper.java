package com.kanban.profile.mapper;

import com.kanban.profile.dto.request.AddressRequest;
import com.kanban.profile.dto.response.AddressResponse;
import com.kanban.profile.entity.Address;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface AddressMapper {
    Address toAddress(AddressRequest request);
    @Mapping(target = "address", ignore = true)
    AddressResponse toAddressResponse(Address address);
    void toUpdateAddress(@MappingTarget Address address , AddressRequest request);
}
