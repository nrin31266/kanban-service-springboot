package com.rin.kanban.mapper;

import com.rin.kanban.dto.request.CartRequest;
import com.rin.kanban.dto.response.CartResponse;
import com.rin.kanban.entity.Cart;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CartMapper {
    Cart toCart(CartRequest cartRequest);
    CartResponse toCartResponse(Cart cart);
    @Mapping(target = "count", ignore = true)
    void updateCart(@MappingTarget Cart cart, CartRequest cartRequest);
}
