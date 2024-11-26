package com.rin.kanban.repository;


import com.rin.kanban.entity.OrderProduct;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface OrderProductsRepository extends MongoRepository<OrderProduct, String> {
    List<OrderProduct> findByOrderId(String orderId);
}
