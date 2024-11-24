package com.kanban.payment.mapper;

import com.kanban.payment.dto.request.OrderRequest;
import com.kanban.payment.dto.response.OrderResponse;
import com.kanban.payment.entity.Order;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    Order toOrder(OrderRequest request);
    OrderResponse toOrderResponse(Order order);
}
