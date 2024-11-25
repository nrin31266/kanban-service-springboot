package com.rin.kanban.mapper;


import com.rin.kanban.dto.request.OrderRequest;
import com.rin.kanban.dto.response.OrderResponse;
import com.rin.kanban.entity.Order;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    Order toOrder(OrderRequest request);
    OrderResponse toOrderResponse(Order order);
}
