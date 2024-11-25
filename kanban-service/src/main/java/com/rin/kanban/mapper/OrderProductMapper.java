package com.rin.kanban.mapper;


import com.rin.kanban.dto.request.OrderProductRequest;
import com.rin.kanban.dto.response.OrderProductResponse;
import com.rin.kanban.entity.OrderProduct;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrderProductMapper {
    OrderProduct toOrderProducts(OrderProductRequest orderProducts);
    OrderProductResponse toOrderProductsResponse(OrderProduct orderProducts);
}
